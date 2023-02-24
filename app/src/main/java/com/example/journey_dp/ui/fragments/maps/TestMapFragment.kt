package com.example.journey_dp.ui.fragments.maps




import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentTestMapBinding
import com.example.journey_dp.ui.adapter.adapters.InputAdapter
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.setMapMenu
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import java.util.*


//TODO: """
// VO FEBRUARI SPRAVIT LOGIKU PLANOVANIA A ZOBRAZOVANIA TRASY
// 5. Implementovanie nadpájania trás roznymi dopravnymi prostriedkami
// 6. Zrozumitelne zobrazovanie napr. informácii o ceste autobusom
// 7. Fetchovanie obrázkov,popisov, url odkazov a inych informácii o Places a pridanie ich do mapy alebo bottom sheetu po kliknuti na place
// 8. Jednoduche zaregistrovanie otvorenia intentu odkazu nejakeho hotelu a zapisanie informacii o ubytovani
// 9. IMPLEMENTOVANIE LOKALNEJ DATABAZY a SHAREDPREFERENCES pre nastavenie prihlaseneho usera pokial sa neodhlasi
// 10. Implementacia Loginu z FIREBASE a INTEGRACIA appky s FIREBASE
// 11. A LOT OF WORK TO END :(
// 12....
// """



//TODO: """ Tento Tyzden
// 1. Implementacia SQL LOKAL DB ????
// 2. ViewModel pre Map Fragment ???
// 4. REST API pre získavanie trasy - zaciatok len pre štart a ciel a zobrazenie na mape ---> Hlavna priorita
// 5. Osetrit ak nie je internet
// """



class TestMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    // Declaration of binding fragment
    private var _binding : FragmentTestMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputAdapter: InputAdapter
    private lateinit var mapViewModel: MapViewModel

    // Declaration of binding google map
    private lateinit var googleMap: GoogleMap
    // Declaration of standard bottom sheet
    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<View>
    // Declaration of Places Client
    private lateinit var places: PlacesClient

    // Declaration of fusedLocationProvider
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Search Fragment late initialization
    private lateinit var searchView: AutocompleteSupportFragment

    // Set default Location : If user not granted permission
    private val defaultLocation = LatLng( 48.14838109999999, 17.1080601)

    private val defaultLocationName = "Bratislava"

    private var permissionStateDenied = false

    private var changeUserLocation = false

    private lateinit var placeFromSearch: Place

    private var isStatusSet: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var status: Status

    private val inputs = mutableListOf<LinearLayout>()
    private var markers = mutableListOf<Marker>()
    private var polylines = mutableListOf<Polyline>()
    private var checkLine: String = ""


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let {
                    placeFromSearch = Autocomplete.getPlaceFromIntent(result.data!!)
                    mapViewModel.setPlaceName(placeFromSearch.name!!)
                    val destination = placeFromSearch.latLng!!.latitude.toString() + "," + placeFromSearch.latLng!!.longitude.toString()
                    inputAdapter.setName(placeFromSearch.name!!, destination)



                    if (changeUserLocation) {
                        binding.myLocationInput.setText(placeFromSearch.name!!)
                        mapViewModel.setLocation(placeFromSearch.latLng!!)
                        val marker = googleMap.addMarker(MarkerOptions().position(placeFromSearch.latLng!!).title(placeFromSearch.name!!).icon(
                            BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
                        ))
                        markers.add(0,marker!!)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                placeFromSearch.latLng!!,
                                15F
                            )
                        )
                    }
                    val position = inputAdapter.getID()

                    Log.i("TEST","Position of inputs from adapter $position and ${markers.size} and $changeUserLocation")

                    if ((!changeUserLocation).and(position >= 0)) {
                        inputAdapter.setPosition(position)
                        showMarkerOnChoosePlace(placeFromSearch.name!!, placeFromSearch.latLng!!, position.plus(1))

                        var origin = ""
                        origin = if (position == 0) {
                            mapViewModel.location.value!!.latitude.toString() + "," + mapViewModel.location.value!!.longitude.toString()
                        } else {
                            inputAdapter.getNewOrigin(position.minus(1))
                        }


                        //TODO: dynamically set mode
                        val mode = "driving"
                        //TODO: dynamically set transit
                        val transit = ""
                        val key = BuildConfig.GOOGLE_MAPS_API_KEY

                        Log.i("TEST", "ORIGIN AND DESTINATION:  $origin and $destination")

                        mapViewModel.getDirections(origin, destination, mode, transit, key)


                        mapViewModel.directions.observe(viewLifecycleOwner, Observer { result ->
                            //TODO: result!!.routes[0].legs[0].distance
                            if (checkLine != result?.routes?.get(0)?.overviewPolyline!!.points) {
                                showRouteOnMap(result?.routes?.get(0)?.overviewPolyline!!.points)
                            }

                        })

                    }

//                    git push origin --delete push
                    binding.directionsLayout.visibility = View.VISIBLE
                    changeUserLocation = false

                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                // TODO: Handle the error.
                result.data?.let {
                    status = Autocomplete.getStatusFromIntent(result.data!!)
                    isStatusSet.postValue(true)
                    Log.i("TEST", status.statusMessage ?: "")
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }

    }



//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize Places Client
        activity?.applicationContext?.let { Places.initialize(it, BuildConfig.GOOGLE_MAPS_API_KEY) }

        mapViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        )[MapViewModel::class.java]

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        getLocation(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // setup fragment view
        _binding = FragmentTestMapBinding.inflate(inflater, container, false)
        setMapMenu(
            activity = requireActivity() ,
            lifecycleOwner = viewLifecycleOwner,
            view = binding.root
        )
        recyclerView = binding.inputsList
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(requireContext())
        val search = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        search.setPlaceFields(listFields)

        searchView = search
        searchView.setActivityMode(AutocompleteActivityMode.FULLSCREEN)

        // keyboard off
        binding.myLocationInput.focusable = View.NOT_FOCUSABLE

        // get places client
        places = activity?.applicationContext?.let { Places.createClient(it) }!!

        // initialized bottom sheet
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetBehavior)
        standardBottomSheetBehavior.apply {
            peekHeight = 80
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }




        searchView.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                if (binding.myLocationInput.text.toString().isNotBlank()) {
                    val marker = markers.getOrNull(0)
                    marker?.remove()
                    markers.removeAt(0)
                }
                googleMap.apply {
                    val marker = addMarker(MarkerOptions().position(place.latLng!!).title(place.name!!).icon(
                        BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
                    ))
                    markers.add(0,marker!!)
                    animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            place.latLng!!,
                            15F
                        )
                    )
                }

                binding.apply {
                    if (myLocationInput.text.toString().isNotBlank()) {
                        myLocationInput.setText(place.name)
                    }
                    else {
                        myLocationInput.setText(place.name)
                    }
                }

            }

            override fun onError(status: Status) {
                Log.i("Place", "An error occurred: $status")
                Toast.makeText(context, "Error ! Please Try again $status", Toast.LENGTH_SHORT ).show()
            }
        })


        recyclerView.layoutManager = LinearLayoutManager(context)
        inputAdapter = InputAdapter("","",inputs,markers,polylines,resultLauncher)
        recyclerView.adapter = inputAdapter
        val layoutView = layoutInflater.inflate(R.layout.destination_item, null)
        val layout: LinearLayout = layoutView.findViewById(R.id.layout_for_add_stop)

        binding.testButton.setOnClickListener {
            inputAdapter.setName("","")
            binding.directionsLayout.visibility = View.GONE
            inputs.add(layout)
            inputAdapter.notifyItemInserted(inputs.size)
            mapViewModel.setValueOfPlace(false)
        }

        binding.myLocationInput.setOnClickListener {
            if (binding.myLocationInput.text.toString().isNotBlank()) {
                val marker = markers.getOrNull(0)
                marker?.remove()
                markers.removeAt(0)
            }
            changeUserLocation = true
            resultLauncher.launch(intent)
        }


    }


    private fun showMarkerOnChoosePlace(locationName: String, coordinates: LatLng, position: Int) {
        val marker = googleMap.addMarker(MarkerOptions().position(coordinates).title(locationName).icon(
            BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
        ))
        markers.add(position,marker!!)

//        standardBottomSheetBehavior.peekHeight = 400
//        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                coordinates,
                15F
            )
        )
    }


    override fun onMapReady(mapG: GoogleMap) {
        googleMap = mapG
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mapViewModel.setLocation(defaultLocation)

        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(defaultLocation)
                .title(defaultLocationName)
        )
        markers.add(0,marker!!)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                defaultLocation,
                15F
            ))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnPoiClickListener(this)
        binding.myLocationInput.setText(defaultLocationName)

    }

    
    override fun onPoiClick(poi: PointOfInterest) {
        //TODO: THIS FUNCTION WILL BE GETTING INFORMATION ABOUT PLACE ON CLICK, NOT TO SET MARKERS
        //TODO: MAX MARKERS WILL BE DIFFERENT FROM PLANNING MARKERS , OR THERE WILL BE INFO WINDOW ON CLICK,WITH SOME TEXT AND IMAGE ABOUT PLACE
        Toast.makeText(context, "Clicked: ${poi.name}", Toast.LENGTH_SHORT).show()
    }

    private fun showRouteOnMap(line: String) {
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        checkLine = line
        if (line.isNotBlank()) {
            val polyline: List<LatLng> = PolyUtil.decode(line)
            val options = PolylineOptions()
            options.width(8F)
            options.color(Color.BLACK)
            options.addAll(polyline)
            val addedPolyline = googleMap.addPolyline(options)

            polylines.add(addedPolyline)

            Log.i("TEST", "AFTER ADDED POLYLINE : $polylines")
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    placeFromSearch.latLng!!,
                    10F
                )
            )
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

