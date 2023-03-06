package com.example.journey_dp.ui.fragments.maps

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentTestMapBinding
import com.example.journey_dp.ui.adapter.adapters.ImageAdapter
import com.example.journey_dp.ui.adapter.adapters.InputAdapter
import com.example.journey_dp.ui.adapter.adapters.StepsAdapter
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.calculateDistanceAndDuration
import com.example.journey_dp.utils.journeyNameDialog
import com.example.journey_dp.utils.setMapMenu
import com.google.android.gms.common.api.Status
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
import com.google.android.material.chip.Chip
import com.google.maps.android.PolyUtil
import java.util.*


// TODO: """
//  -----------------------------------------------------------------------------------
//  Pre zobrazovanie ulozenych trás v profile vytvorit DB - ENTITIES, staci jedna? a to :
//  Nazov vyletu - vsetky destinacie a to cca typom - [origin, travel mode, destination].. plus poznamky ku kazdej trase, .. nasledne
//  po kliknuti na karticku vyletu vytiahnut z databazy tieto informacie, prejst loopom cez ne a vykreslit na mapu pricom
//  po kliknuti na karticku zobrazit najskôr ikonku loadovania a potom zobrazit danu trasu uz.
//  Po finish buttone automaticky ulozit tieto info do DB - mozno zobrazit animaciu loadovania a nasledne presmerovanie
//  do profilu na karticku pricom pri kazdej karticke bude button na zdielanie (ikonka) kde sa pouzivatelovi zobrazia moznosti zdielania
//  _______________________________________________________________________________________
//  DOKONCENIE ZISKANIA AKTUALNEJ POLOHY USERA
//  ________________________________________________________________________________________
//  Ako posledne spravit logovanie do aplikacie cez gmail ucet - vyuzitie firebase
//  OTESTOVANIE a OSETRENIE
//  STYLOVANIE
//  """

class TestMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    // Declaration of binding fragment
    private var _binding : FragmentTestMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewImage: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewSteps: RecyclerView

    private lateinit var inputAdapter: InputAdapter
    private lateinit var mapViewModel: MapViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var stepsAdapter: StepsAdapter

    // Declaration of binding google map
    private lateinit var googleMap: GoogleMap
    // Declaration of standard bottom sheet
    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<View>
    // Declaration of Places Client
    private lateinit var places: PlacesClient


    // Search Fragment late initialization
    private lateinit var searchView: AutocompleteSupportFragment

    private lateinit var placeFromSearch: Place

    private lateinit var status: Status


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let {
                    placeFromSearch = Autocomplete.getPlaceFromIntent(result.data!!)


                    if (mapViewModel.changeUserLocation) {
                        binding.myLocationInput.setText(placeFromSearch.name!!)
                        mapViewModel.setLocation(placeFromSearch.latLng!!)
                        val marker = googleMap.addMarker(MarkerOptions().position(placeFromSearch.latLng!!).title(placeFromSearch.name!!).icon(
                            BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
                        ))
                        mapViewModel.markers.add(0,marker!!)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                placeFromSearch.latLng!!,
                                15F
                            )
                        )

                        val changedOrigin = placeFromSearch.latLng!!.latitude.toString() + "," + placeFromSearch.latLng!!.longitude.toString()
                        if (mapViewModel.polylines.isNotEmpty()) {
                            val firstDestination = inputAdapter.getNewOrigin(0)
                            var counter = 0
                            for (line in mapViewModel.polylines) {
                                if (counter == 0) {
                                    line.remove()
                                }
                                counter+=1
                            }
                            if (mapViewModel.infoMarkers.size > 0) {
                                val infoMark = mapViewModel.infoMarkers.getOrNull(0)
                                infoMark?.remove()
                                mapViewModel.infoMarkers.removeAt(0)
                                mapViewModel.polylines.removeAt(0)
                            }
                            Log.i("MYTEST", "USER LOC: $changedOrigin and $firstDestination")
                            mapViewModel.getDirections(changedOrigin, firstDestination, "driving", "", BuildConfig.GOOGLE_MAPS_API_KEY)
                            mapViewModel.directions.observe(viewLifecycleOwner) { result ->
                                if (result != null) {
                                    if (result.routes!!.isNotEmpty()) {
                                        val comparison = (mapViewModel.checkLine == result.routes[0].overviewPolyline.points)
                                        if (!comparison) {
                                            recyclerViewSteps.adapter = stepsAdapter
                                            binding.stepsScrollView.visibility = View.VISIBLE
                                            mapViewModel.stepsList.add(result.routes[0].legs[0].steps)
                                            stepsAdapter.submitList(result.routes[0].legs[0].steps)
                                            showRouteOnMap(result.routes[0].overviewPolyline.points, result.routes[0].legs[0].distance.text,
                                                result.routes[0].legs[0].duration.text, mapViewModel.iconType.value!!,0)
                                            mapViewModel.changeUserLocation = false
                                        }
                                    }

                                }
                            }
                        }
                    }

                    var destination = placeFromSearch.latLng!!.latitude.toString() + "," + placeFromSearch.latLng!!.longitude.toString()
                    var position = inputAdapter.getID()
                    Log.i("MYTEST", "FIRST POSITION : $position")
                    if ((!mapViewModel.changeUserLocation).and(position >= 0)) {
                        inputAdapter.setName(placeFromSearch.name!!, destination)
                        inputAdapter.addPlaceId(placeFromSearch.id!!)
                        inputAdapter.setPosition(position)
                        showMarkerOnChoosePlace(placeFromSearch.name!!, placeFromSearch.latLng!!, position.plus(1))


                        var origin = if (position == 0) {
                            mapViewModel.location.value!!.latitude.toString() + "," + mapViewModel.location.value!!.longitude.toString()
                        } else {
                            inputAdapter.getNewOrigin(position.minus(1))
                        }
                        Log.i("MYTEST","ORIGIN WAS SET TO : $origin")

                        // HELPER VARIABLES
                        var mode = "driving"
                        var transit = ""
                        val key = BuildConfig.GOOGLE_MAPS_API_KEY
                        var points: String
                        var distance: String
                        var duration: String
                        var iconType: String
                        var comparison: Boolean

                        Log.i("MYTEST", "ORIG, DEST : $origin and $destination")
                        mapViewModel.getDirections(origin, destination, mode, transit, key)

                        mapViewModel.directions.observe(viewLifecycleOwner) { result ->
                            try {
                                if (result != null) {
                                    if (result.routes!!.isNotEmpty()) {
                                        comparison = (mapViewModel.checkLine == result.routes[0].overviewPolyline.points)
                                        if (!comparison) {
                                            Log.i("MYTEST", "SPUSTAM DIRECTIONS")
                                            points = result.routes[0].overviewPolyline.points
                                            distance = result.routes[0].legs[0].distance.text
                                            duration = result.routes[0].legs[0].duration.text
                                            iconType = mapViewModel.iconType.value!!
                                            position = inputAdapter.getID()

                                            Log.i("MYTEST", "ADD TRAVEL MODE AT $position and $iconType")
                                            mapViewModel.travelMode.add(position, iconType)

                                            recyclerViewSteps.adapter = stepsAdapter
                                            binding.stepsScrollView.visibility = View.VISIBLE
                                            mapViewModel.stepsList.add(result.routes[0].legs[0].steps)
                                            stepsAdapter.submitList(result.routes[0].legs[0].steps)
                                            showRouteOnMap(points, distance, duration, iconType,position)
                                        }
                                    }

                                }
                            }
                            catch (e: Exception) {
                                Log.e("MYTEST", "ERROR : ${e.message}")
                            }
                        }


                        binding.chipGroupDirections.setOnCheckedStateChangeListener { group, checkedIds ->
                                checkedIds.map {
                                    val chip: Chip? = group.findViewById(it)
                                    mode = chip?.tag.toString()

                                    mapViewModel.setIconType(mode)
                                    if ((mode == "bus").or(mode == "train")) {
                                        mode = "transit"
                                        transit = chip?.tag.toString()
                                    }
                                    if ((mode == "driving").or(mode == "walking").or(mode == "bicycling")) {
                                        transit = ""
                                    }
                                    position = inputAdapter.getID()


                                    if (mapViewModel.stepsList.isNotEmpty().and(position != -1)) {
                                        if (position <= mapViewModel.stepsList.size.minus(1)) {
                                            Log.i("MYTEST", "DELETE AT POSITION STEPS LIST: $position and ${mapViewModel.stepsList.size}")
                                            mapViewModel.stepsList.removeAt(position)
                                        }
                                    }
                                    if (position != 0) {
                                        origin =  inputAdapter.getNewOrigin(position.minus(1))
                                        destination = inputAdapter.getNewOrigin(position)
                                        Log.i("MYTEST", "FIRST POSITION : $position and origin and destination is $origin and $destination")
                                    }
                                    if ((position == 0).and(mapViewModel.polylines.size == 1).and(mapViewModel.infoMarkers.size == 1)) {
                                        origin =  binding.myLocationInput.text.toString()
                                        destination = inputAdapter.getNewOrigin(position)
                                        Log.i("MYTEST", "SECOND POSITION : $position and origin and destination is $origin and $destination")

                                    }


                                    Log.i("MYTEST", "ORIG, DEST IN CHIPS : $origin and $destination")
                                    mapViewModel.getDirections(origin, destination, mode, transit, key)
                                    if (mapViewModel.polylines.isNotEmpty()) {
                                        Log.i("MYTEST", "POlYLINES IN CHIPS BEFORE DELETE: ${mapViewModel.polylines}")
                                        var counter = 0
                                        for (line in mapViewModel.polylines) {
                                            if (counter == position) {
                                                line.remove()
                                            }
                                            counter+=1
                                        }
                                        if (position <= mapViewModel.infoMarkers.size.minus(1)) {
                                            val infoMark = mapViewModel.infoMarkers.getOrNull(position)
                                            infoMark?.remove()
                                            mapViewModel.infoMarkers.removeAt(position)
                                            mapViewModel.polylines.removeAt(position)
                                            Log.i("MYTEST", "POlYLINES IN CHIPS AFTER DELETE: ${mapViewModel.polylines}")
                                        }
                                        if (position <= mapViewModel.travelMode.size.minus(1)) {
                                            mapViewModel.travelMode.removeAt(position)
                                        }

                                    }
                                }

                            }
                        }


                        binding.chipGroupDirections.visibility = View.VISIBLE
                        mapViewModel.changeUserLocation = false

                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                result.data?.let {
                    status = Autocomplete.getStatusFromIntent(result.data!!)
                    Log.i("TEST", status.statusMessage ?: "")
                    Toast.makeText(context,"Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.applicationContext?.let { Places.initialize(it, BuildConfig.GOOGLE_MAPS_API_KEY) }

        mapViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[MapViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[ProfileViewModel::class.java]
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestMapBinding.inflate(inflater, container, false)
        setMapMenu(
            activity = requireActivity() ,
            lifecycleOwner = viewLifecycleOwner,
            view = binding.root
        )
        recyclerView = binding.inputsList
        recyclerViewSteps = binding.recyclerViewSteps
        recyclerViewImage = binding.imageRecyclerView
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

        binding.myLocationInput.focusable = View.NOT_FOCUSABLE

        places = activity?.applicationContext?.let { Places.createClient(it) }!!

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetBehavior)
        standardBottomSheetBehavior.apply {
            peekHeight = 80
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        searchView.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                if (binding.myLocationInput.text.toString().isNotBlank()) {
                    val marker = mapViewModel.markers.getOrNull(0)
                    marker?.remove()
                    mapViewModel.markers.removeAt(0)
                }
                googleMap.apply {
                    val marker = addMarker(MarkerOptions().position(place.latLng!!).title(place.name!!).icon(
                        BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
                    ))
                    mapViewModel.markers.add(0,marker!!)
                    mapViewModel.setLocation(place.latLng!!)
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

        recyclerViewSteps.layoutManager = LinearLayoutManager(context)
        stepsAdapter = StepsAdapter()

        imageAdapter = ImageAdapter()
        recyclerViewImage.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        recyclerView.layoutManager = LinearLayoutManager(context)
        inputAdapter = InputAdapter(binding.root,stepsAdapter, recyclerViewSteps, imageAdapter,recyclerViewImage,"","",
            mapViewModel,standardBottomSheetBehavior,places,resultLauncher)
        recyclerView.adapter = inputAdapter

        val layoutView = layoutInflater.inflate(R.layout.destination_item, null)
        val layout: LinearLayout = layoutView.findViewById(R.id.layout_for_add_stop)

        binding.testButton.setOnClickListener {
            inputAdapter.setName("","")
            binding.chipGroupDirections.clearCheck()
            binding.chipGroupDirections.visibility = View.GONE
            mapViewModel.notes.add("")
            mapViewModel.setIconType("driving")
            mapViewModel.inputs.add(layout)
            inputAdapter.notifyItemInserted(mapViewModel.inputs.size)
        }

        binding.myLocationInput.setOnClickListener {
            if (binding.myLocationInput.text.toString().isNotBlank()) {
                val marker = mapViewModel.markers.getOrNull(0)
                marker?.remove()
                mapViewModel.markers.removeAt(0)
            }

            mapViewModel.changeUserLocation = true
            resultLauncher.launch(intent)
        }

        val nameDialog = journeyNameDialog(requireActivity(), mapViewModel, profileViewModel,inputAdapter.getAllDestinations(), binding.root)
        binding.finishButton.setOnClickListener {
            nameDialog.show()
        }

    }


    private fun showMarkerOnChoosePlace(locationName: String, coordinates: LatLng, position: Int) {
        val marker = googleMap.addMarker(MarkerOptions().position(coordinates).title(locationName).icon(
            BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
        ))
        mapViewModel.markers.add(position,marker!!)

        standardBottomSheetBehavior.peekHeight = 80
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

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

        mapViewModel.setLocation(mapViewModel.defaultLocation)

        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(mapViewModel.defaultLocation)
                .title(mapViewModel.defaultLocationName)
        )
        mapViewModel.markers.add(0,marker!!)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                mapViewModel.defaultLocation,
                15F
            ))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnPoiClickListener(this)
        binding.myLocationInput.setText(mapViewModel.defaultLocationName)

    }

    override fun onPoiClick(poi: PointOfInterest) {
        Toast.makeText(context, "Clicked: ${poi.name}", Toast.LENGTH_SHORT).show()
    }

    private fun showRouteOnMap(line: String, distanceText: String, durationText: String, choosedIcon: String, position: Int) {
        val color = when(choosedIcon){
            "driving" -> Color.BLUE
            "bus" -> Color.rgb(210, 152, 121)
            "train" -> Color.rgb(187, 62, 146)
            "walking" -> Color.rgb(32, 96, 70)
            "bicycling" -> Color.rgb(176,129,213)
            else -> Color.BLUE
        }

        mapViewModel.checkLine = line
        if (line.isNotBlank()) {
            Log.i("MYTEST", "CREATE POLYLINE line is : $line")
            val polyline: List<LatLng> = PolyUtil.decode(line)

            val options = PolylineOptions()
            options.width(10F)
            options.color(color)
            options.addAll(polyline)
            val addedPolyline = googleMap.addPolyline(options)
            addedPolyline.addInfoWindow(googleMap,distanceText,durationText,choosedIcon)

            Log.i("MYTEST", "POLYLINES BEFORE ADD: ${mapViewModel.polylines}")
            mapViewModel.polylines.add(addedPolyline)
            Log.i("MYTEST", "POLYLINES AFTER ADD: ${mapViewModel.polylines}")


            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    mapViewModel.markers[position.plus(1)].position,
                    10F
                )
            )
            calculateDistanceAndDuration(mapViewModel.infoMarkers, binding.root)
        }
    }


    private fun Polyline.addInfoWindow(map: GoogleMap, title: String, message: String, iconType: String) {
        val pointsOnLine = this.points.size
        val infoLatLng = this.points[(pointsOnLine / 2)]

        val iconMarker = when(iconType){
            "driving" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_directions_car_24)
            "bus" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_directions_bus_24)
            "train" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_directions_transit_filled_24)
            "walking" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_directions_walk_24)
            "bicycling" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_directions_bike_24)
            else -> bitmapDescriptorFromVector(R.drawable.ic_baseline_mode_of_travel_24)
        }
        val info: Marker? = map.addMarker(
            MarkerOptions()
                .position(infoLatLng)
                .title(title)
                .snippet(message)
                .alpha(1f)
                .icon(iconMarker)
                .anchor(0f, 0f)
        )
        info?.showInfoWindow()
        mapViewModel.infoMarkers.add(info!!)
    }

    private fun  bitmapDescriptorFromVector(vectorResId:Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId);
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight);
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888);
        val canvas =  Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

