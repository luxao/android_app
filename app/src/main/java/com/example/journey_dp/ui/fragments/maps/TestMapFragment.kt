package com.example.journey_dp.ui.fragments.maps



import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentTestMapBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import java.util.*


//TODO: """
// VO FEBRUARI SPRAVIT LOGIKU PLANOVANIA A ZOBRAZOVANIA TRASY
// 1.Nadesignovat a implementovat bottom sheet  -> UPDATE: Implementácia DONE, DESIGN NOT YET //
// 2. Doimplementovať vyhladavanie  -> UPDATE: Implementacia vyhladavanie pripravena na použitie in future
// 3. Doimplementovať získanie polohy usera a nastavenie ako zač. poloha --> UPDATE : Viacmenej pripravene
// 4. Implementovanie pridavania do listu alebo implementovanie zobrazovania trasy pre dve miesta od A do B
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
// 3. UPDATE designu celeho Planovania - navrhnutie ---> Hlavna priorita
// 4. REST API pre získavanie trasy - zaciatok len pre štart a ciel a zobrazenie na mape ---> Hlavna priorita
// 5. RecyclerView nastudovanie a implementovanie pridavania do Listu a zobrazovanie v realnom čase.
// 10.2.2022 Task for the day
// Autocomplete search set on intent to click listener on every input
// Pass values from place autocomplete to input where was clicked
// """



class TestMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    // Declaration of binding fragment
    private var _binding : FragmentTestMapBinding? = null
    private val binding get() = _binding!!

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
    private val defaultLocation = LatLng( 48.148598, 17.107748)

    private val defaultLocationName = "Bratislava"

    private var permissionStateDenied = false

    private lateinit var placeFromSearch: Place
    private var isPlaceSet: MutableLiveData<Boolean> = MutableLiveData(false)

    private var isStatusSet: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var status: Status


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let {
                    isPlaceSet.postValue(true)
                    placeFromSearch = Autocomplete.getPlaceFromIntent(result.data!!)
                    Log.i("TEST", "Place: ${placeFromSearch.name}, ${placeFromSearch.id}")
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


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getLocation(requireContext())
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getLocation(requireContext())
            }
            else -> {
            // No location access granted.
                permissionStateDenied = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize Places Client
        activity?.applicationContext?.let { Places.initialize(it, BuildConfig.GOOGLE_MAPS_API_KEY) }
        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // setup fragment view
        _binding = FragmentTestMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        val search = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        search.setPlaceFields(listFields)

        searchView = search

        searchView.setActivityMode(AutocompleteActivityMode.FULLSCREEN)

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
                Log.i("Place", "Place: ${place.name}, ${place.id}, ${place.latLng?.latitude}, ${place.latLng?.longitude}")

                if (binding.myLocationInput.text.toString().isNotBlank()) {
                    binding.myLocationInput.text!!.clear()
                    binding.myLocationInput.append(place.name)
                }

            }

            override fun onError(status: Status) {
                Log.i("Place", "An error occurred: $status")
                Toast.makeText(context, "Error ! Please Try again $status", Toast.LENGTH_SHORT ).show()
            }

        })

        val searchIntent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(requireContext())

        binding.apply {
            clickableInputs(inputStop, searchIntent)

        }


        isStatusSet.observe(viewLifecycleOwner) {
            if (this::status.isInitialized) {
                Toast.makeText(context, "Please, Try Again. Something went wrong", Toast.LENGTH_SHORT).show()
                isStatusSet.postValue(false)
            }
        }


    }


    private fun clickableInputs(input: TextInputEditText, intent: Intent) {


            input.setOnClickListener {
                resultLauncher.launch(intent)


                isPlaceSet.observe(viewLifecycleOwner) {
                    if (this::placeFromSearch.isInitialized) {
                        if (input.text.toString().isNotBlank()) {
                            input.text?.clear()
                        }
                        val placeName = placeFromSearch.name
                        isPlaceSet.postValue(false)
                        input.append(placeName)
                    }
                    isPlaceSet.postValue(false)
                }
            }




    }





    override fun onMapReady(mapG: GoogleMap) {
        googleMap = mapG
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        googleMap.addMarker(
            MarkerOptions()
                .position(defaultLocation)
                .title(defaultLocationName)
        )

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                defaultLocation,
                15F
            ))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnPoiClickListener(this)
    }

    
    override fun onPoiClick(poi: PointOfInterest) {
        Toast.makeText(context, """Clicked: ${poi.name}
            Latitude:${poi.latLng.latitude} Longitude:${poi.latLng.longitude}""",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Check if location is enabled or not and return boolean
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }


    @SuppressLint("MissingPermission")
    fun getLocation(context: Context){
        if (checkPermissions(context)) {
                if (isLocationEnabled()) {
                    fusedLocationProviderClient.getCurrentLocation(
                        CurrentLocationRequest.Builder().setDurationMillis(30000)
                            .setMaxUpdateAgeMillis(60000).build(), null
                    ).addOnSuccessListener {
                        it?.let {
                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            //TODO: getFromLocation je deprecated pre Android 11, treba od novsej verzie pouzit novu funkciu
                            val addresses: List<Address>? = geocoder.getFromLocation(it.latitude, it.longitude,1)
                            val cityName: String = addresses!![0].getAddressLine(0)
                            // Vycistenie google mapy od default nastaveni
                            googleMap.clear()
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(it.latitude, it.longitude))
                                    .title(cityName)
                            )

                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(it.latitude, it.longitude),
                                    15F
                                ))
                            binding.myLocationInput.append(cityName)
                        }
                    }
                }
            else {
                Toast.makeText(context,"Please turn on location", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            requestPermissions()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

