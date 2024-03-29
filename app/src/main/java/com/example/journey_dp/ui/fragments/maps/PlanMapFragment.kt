package com.example.journey_dp.ui.fragments.maps


import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.data.room.model.RouteEntity
import com.example.journey_dp.databinding.FragmentPlanMapBinding
import com.example.journey_dp.ui.adapter.adapters.DetailsJourneyAdapter
import com.example.journey_dp.ui.adapter.adapters.ImageAdapter
import com.example.journey_dp.ui.adapter.adapters.InputAdapter
import com.example.journey_dp.ui.adapter.adapters.StepsAdapter
import com.example.journey_dp.ui.fragments.journey.NotificationsFragmentDirections
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.*
import com.example.journey_dp.utils.shared.LocationSharedPreferencesUtil
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.CurrentLocationRequest
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
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import com.google.maps.android.PolyUtil
import java.util.*


class PlanMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    // Declaration of binding fragment
    private var _binding : FragmentPlanMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref : DatabaseReference

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val navigationArgs: PlanMapFragmentArgs by navArgs()

    private lateinit var detailsJourneyAdapter: DetailsJourneyAdapter
    private lateinit var detailsRecyclerView: RecyclerView

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
                try {
                    result.data?.let {
                        placeFromSearch = Autocomplete.getPlaceFromIntent(result.data!!)

                        var position = inputAdapter.getID()


                        if (mapViewModel.changeBetweenWaypoints) {

                            val marker = mapViewModel.markers.getOrNull(position.plus(1))
                            marker?.remove()
                            mapViewModel.markers.removeAt(position.plus(1))
                            if (mapViewModel.polylines.isNotEmpty()) {
                                var counter = 0
                                for (line in mapViewModel.polylines) {
                                    if (counter == position) {
                                        line.remove()
                                    }
                                    counter+=1
                                }
                                val infoMark = mapViewModel.infoMarkers.getOrNull(position)
                                infoMark?.remove()
                                mapViewModel.infoMarkers.removeAt(position)
                                mapViewModel.polylines.removeAt(position)
                            }

                            if (mapViewModel.newOrigin[position.plus(1)].isNotEmpty()) {
                                mapViewModel.callNewDirections = true
                                mapViewModel.nextDestination = mapViewModel.newOrigin[position.plus(1)]
                            }

                            if (mapViewModel.newOrigin.isNotEmpty()) {
                                mapViewModel.newOrigin.removeAt(position)
                                mapViewModel.destinationsName.removeAt(position.plus(1))
                            }

                            mapViewModel.placeIds.removeAt(position)

                            if (mapViewModel.notes[position].isNotEmpty()) {
                                mapViewModel.notes.removeAt(position)
                            }


                            mapViewModel.bitmapList.removeAt(position)
                            mapViewModel.addressList.removeAt(position)
                            mapViewModel.phoneList.removeAt(position)
                            mapViewModel.websiteList.removeAt(position)

                            calculateDistanceAndDuration(mapViewModel.infoMarkers, binding.root)
                            mapViewModel.changeBetweenWaypoints = false
                        }

                        if (mapViewModel.changeUserLocation) {
                            binding.myLocationInput.setText(placeFromSearch.name!!)
                            if (mapViewModel.destinationsName[0].isNotEmpty()) {
                                mapViewModel.destinationsName.removeAt(0)
                            }


                            mapViewModel.location = placeFromSearch.latLng!!
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
                            mapViewModel.destinationsName.add(0, placeFromSearch.name!!)
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
                                        else {
                                            Toast.makeText(context,"This path does not exist ! Please choose another transport", Toast.LENGTH_LONG).show()
                                        }

                                    }
                                }
                            }
                        }

                        var destination = placeFromSearch.latLng!!.latitude.toString() + "," + placeFromSearch.latLng!!.longitude.toString()
                        var origin = ""
                        position = inputAdapter.getID()

                        if ((!mapViewModel.changeUserLocation).and(position >= 0)) {
                            inputAdapter.setName(placeFromSearch.name!!, destination)
                            inputAdapter.addPlaceId(placeFromSearch.id!!)
                            inputAdapter.setPosition(position)
                            showMarkerOnChoosePlace(placeFromSearch.name!!, placeFromSearch.latLng!!, position.plus(1))



                            origin = if (position == 0) {
                                if (mapViewModel.location != LatLng(0.0,0.0)) {
                                    mapViewModel.location.latitude.toString() + "," + mapViewModel.location.longitude.toString()
                                } else {
                                    val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(requireContext(),auth.currentUser!!.uid)
                                    val tmpLoc = userLoc!!.split("=")[1]
                                    tmpLoc.split(",")[0] + "," + tmpLoc.split(",")[1]
                                }
                            } else {
                                inputAdapter.getNewOrigin(position.minus(1))
                            }


                            // HELPER VARIABLES
                            var mode = mapViewModel.iconType.value
                            var transit = ""
                            val key = BuildConfig.GOOGLE_MAPS_API_KEY
                            var points: String
                            var distance: String
                            var duration: String
                            var iconType: String
                            var comparison: Boolean


                            mapViewModel.getDirections(origin, destination, mode!!, transit, key)

                            mapViewModel.directions.observe(viewLifecycleOwner) { result ->
                                try {
                                    if (result != null) {
                                        if (result.routes!!.isNotEmpty()) {

                                            comparison = (mapViewModel.checkLine == result.routes[0].overviewPolyline.points)

                                            if (!comparison) {



                                                points = result.routes[0].overviewPolyline.points
                                                distance = result.routes[0].legs[0].distance.text
                                                duration = result.routes[0].legs[0].duration.text
                                                iconType = mapViewModel.iconType.value!!
                                                position = inputAdapter.getID()


                                                mapViewModel.travelMode.add(position, iconType)

                                                recyclerViewSteps.adapter = stepsAdapter
                                                binding.stepsScrollView.visibility = View.VISIBLE
                                                mapViewModel.stepsList.add(result.routes[0].legs[0].steps)
                                                stepsAdapter.submitList(result.routes[0].legs[0].steps)
                                                showRouteOnMap(points, distance, duration, iconType,position)

                                            }
                                        }
                                        else {
                                            Toast.makeText(context,"This path does not exist ! Please choose another transport", Toast.LENGTH_LONG).show()
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

                                    mapViewModel.setIconType(mode!!)
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

                                            mapViewModel.stepsList.removeAt(position)
                                        }
                                    }
                                    if (position != 0) {
                                        origin =  inputAdapter.getNewOrigin(position.minus(1))
                                        destination = inputAdapter.getNewOrigin(position)

                                    }
                                    if ((position == 0).and(mapViewModel.polylines.size == 1).and(mapViewModel.infoMarkers.size == 1)) {
                                        origin =  binding.myLocationInput.text.toString()
                                        destination = inputAdapter.getNewOrigin(position)
                                    }


                                    mapViewModel.setLine("")
                                    mapViewModel.getDirections(origin, destination, mode!!, transit, key)
                                    if (mapViewModel.polylines.isNotEmpty()) {
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
                                        }
                                        if (position <= mapViewModel.travelMode.size.minus(1)) {
                                            mapViewModel.travelMode.removeAt(position)
                                        }

                                    }
                                }

                            }

                            if (mapViewModel.callNewDirections) {
                                if (mapViewModel.polylines.isNotEmpty()) {
                                    var counter = 0
                                    for (line in mapViewModel.polylines) {
                                        if (counter == position) {
                                            line.remove()
                                        }
                                        counter+=1
                                    }
                                    val infoMark = mapViewModel.infoMarkers.getOrNull(position)
                                    infoMark?.remove()
                                    mapViewModel.infoMarkers.removeAt(position)
                                    mapViewModel.polylines.removeAt(position)
                                }

                                calculateDistanceAndDuration(mapViewModel.infoMarkers, binding.root)


                                mapViewModel.getDirections(destination, mapViewModel.nextDestination, "driving", "", BuildConfig.GOOGLE_MAPS_API_KEY)
                            }


                        }

                        binding.chipGroupDirections.visibility = View.VISIBLE
                        mapViewModel.changeUserLocation = false

                    }
                }
                catch(e: NullPointerException) {
                    Log.e("MYTEST", "NullPointerException : $e")

                    Log.e("MYTEST", "NullPointerException : ${e.stackTrace}")
                    Log.e("MYTEST","NullPointerException : ${e.localizedMessage}")
                }
                catch (e: OutOfMemoryError) {
                    Log.e("MYTEST", "OutOfMemoryError : $e")

                    Log.e("MYTEST", "OutOfMemoryError : ${e.stackTrace}")
                    Log.e("MYTEST","OutOfMemoryError : ${e.localizedMessage}")
                }
                catch (e: IllegalStateException) {
                    Log.e("MYTEST", "IllegalStateException : $e")

                    Log.e("MYTEST", "IllegalStateException : ${e.stackTrace}")
                    Log.e("MYTEST","IllegalStateException : ${e.localizedMessage}")
                }
                catch (e: Exception) {
                    Log.e("MYTEST", "Exception : $e")

                    Log.e("MYTEST", "Exception : ${e.stackTrace}")
                    Log.e("MYTEST","Exception : ${e.localizedMessage}")
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                result.data?.let {
                    status = Autocomplete.getStatusFromIntent(result.data!!)
                    Toast.makeText(context,"SEARCHING WAS CANCELED", Toast.LENGTH_SHORT).show()
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
                Toast.makeText(context,"SEARCHING WAS CANCELED", Toast.LENGTH_SHORT).show()
                Log.e("MYTEST", "USER CANCELNUL VYHLADAVANIE")
            }
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        activity?.applicationContext?.let { Places.initialize(it, BuildConfig.GOOGLE_MAPS_API_KEY) }

        mapViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[MapViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[ProfileViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation(requireContext(),fusedLocationProviderClient, mapViewModel, auth.currentUser!!.uid)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanMapBinding.inflate(inflater, container, false)

        recyclerView = binding.inputsList
        recyclerViewSteps = binding.recyclerViewSteps
        recyclerViewImage = binding.imageRecyclerView
        detailsRecyclerView = binding.detailsListRecyclerview
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(requireContext(),auth.currentUser!!.uid)
            if (userLoc!!.isEmpty().or(userLoc.isBlank())) {
                getLocation(requireContext(),fusedLocationProviderClient, mapViewModel, auth.currentUser!!.uid)
            }
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
            mapFragment.getMapAsync(this)

            val menu = binding.topAppBar.menu
            val profileItem = menu.findItem(R.id.profile)
            val imageItem = profileItem?.actionView as ImageView
            Glide.with(requireContext()).load(auth.currentUser?.photoUrl).centerInside().into(imageItem)
            imageItem.setOnClickListener {
                val action = PlanMapFragmentDirections.actionPlanMapFragmentToProfileFragment2()
                view.findNavController().navigate(action)
            }


            binding.topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.home -> {
                        val action = PlanMapFragmentDirections.actionPlanMapFragmentToPlanJourneyFragment()
                        view.findNavController().navigate(action)
                        true
                    }
                    R.id.profile -> {
                        val action = PlanMapFragmentDirections.actionPlanMapFragmentToProfileFragment2()
                        view.findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }


            val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            places = activity?.applicationContext?.let { Places.createClient(it) }!!

            standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetBehavior)

            binding.lifecycleOwner = this@PlanMapFragment.viewLifecycleOwner
            binding.model = this@PlanMapFragment.mapViewModel
            binding.viewmodel = this@PlanMapFragment.profileViewModel

            if ((navigationArgs.id == 0L).and(navigationArgs.shared.isNotBlank()).and(navigationArgs.flag == "share")) {

                detailsJourneyAdapter = DetailsJourneyAdapter()

                binding.chipsInfoMarkersWrapper.visibility = View.GONE
                binding.searchWrapper.visibility = View.GONE
                binding.layoutForAddStation.visibility = View.GONE
                binding.planWrapper.visibility = View.GONE
                binding.placeWrapperInfo.visibility = View.VISIBLE
                val key = BuildConfig.GOOGLE_MAPS_API_KEY
                var mode = ""
                var transit = ""
                var comparison: Boolean
                val detailsOfRoute: MutableList<RouteEntity> = mutableListOf()
                val routes = navigationArgs.shared.split("_")
                var counter = 1L
                var position = 0
                detailsRecyclerView.layoutManager = LinearLayoutManager(context)
                for (element in routes) {
                    val route = element.split("|")
                    val orig = route[0]
                    val dest = route[1]
                    val travel = route[2]

                    if ((travel == "bus").or(travel == "train")) {
                        mode = "transit"
                        transit = travel
                    }
                    else {
                        mode = travel
                        transit = ""
                    }

                    standardBottomSheetBehavior.maxHeight = 400
                    standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    mapViewModel.getDirections(orig, dest, mode, transit, key)

                    detailsRecyclerView.adapter = detailsJourneyAdapter

                    mapViewModel.directions.observe(viewLifecycleOwner) { result ->
                        try {
                            if (result != null) {
                                if (result.routes!!.isNotEmpty()) {

                                    comparison = (mapViewModel.checkLine == result.routes[0].overviewPolyline.points)
                                    if (!comparison) {
                                        val points = result.routes[0].overviewPolyline.points
                                        val distance = result.routes[0].legs[0].distance.text
                                        val duration = result.routes[0].legs[0].duration.text
                                        val originName = result.routes[0].legs[0].startAddress
                                        val originLatLng = LatLng(result.routes[0].legs[0].startLocation!!.lat, result.routes[0].legs[0].startLocation!!.lng)
                                        val destinationLatLng = LatLng(result.routes[0].legs[0].endLocation!!.lat, result.routes[0].legs[0].endLocation!!.lng)
                                        val destinationName = result.routes[0].legs[0].endAddress
                                        val routeDetail = RouteEntity(
                                            id = counter,
                                            journeyId = counter,
                                            origin = originLatLng.toString(),
                                            destination = destinationLatLng.toString(),
                                            travelMode = travel,
                                            note = "",
                                            originName = originName!!,
                                            destinationName = destinationName!!
                                        )
                                        detailsOfRoute.add(position, routeDetail)
                                        counter += 1L
                                        position += 1


                                        showMarkerOnChoosePlace(originName,originLatLng, 0)
                                        showMarkerOnChoosePlace(destinationName,destinationLatLng, 0)
                                        showRouteOnMap(points, distance, duration,  mapViewModel.iconType.value!!, 0)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MYTEST", "ERROR : ${e.localizedMessage}")
                            Log.e("MYTEST", "ERROR : ${e.message}")
                        }
                        if (detailsOfRoute.size == routes.size) {
                            if (detailsOfRoute[0].originName.contains(mapViewModel.defaultLocationName)) {
                                detailsJourneyAdapter.submitList(detailsOfRoute)
                            }
                            else {
                                detailsJourneyAdapter.submitList(detailsOfRoute.asReversed())
                            }
                        }
                    }
                }

                binding.backToProfileBtn.setOnClickListener {
                    val action = PlanMapFragmentDirections.actionPlanMapFragmentToProfileFragment2()
                    view.findNavController().navigate(action)
                }
            }

            if ((navigationArgs.id != 0L).and(navigationArgs.flag == "show")) {
                detailsJourneyAdapter = DetailsJourneyAdapter()

                standardBottomSheetBehavior.apply {
                    peekHeight = 120
                    this.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                profileViewModel.journeyId.postValue(navigationArgs.id)
                binding.searchWrapper.visibility = View.GONE
                binding.chipsInfoMarkersWrapper.visibility = View.GONE
                binding.layoutForAddStation.visibility = View.GONE
                binding.planWrapper.visibility = View.GONE
                binding.placeWrapperInfo.visibility = View.VISIBLE

                detailsRecyclerView.layoutManager = LinearLayoutManager(context)

                var comparison: Boolean
                profileViewModel.journeyWithRoutes.observe(viewLifecycleOwner) {
                    val key = BuildConfig.GOOGLE_MAPS_API_KEY
                    var transit = ""
                    var mode = ""
                    var origin = ""

                    detailsRecyclerView.adapter = detailsJourneyAdapter
                    detailsJourneyAdapter.submitList(it.routes)
                    for (route in it.routes) {
                        if ((route.travelMode == "bus").or(route.travelMode == "train")) {
                            mode = "transit"
                            transit = route.travelMode
                        } else {
                            mode = route.travelMode
                            transit = ""
                        }
                        origin = if (route.origin.contains("lat/lng:")) {
                            route.origin.split("(")[1].split(")")[0]
                        } else {
                            route.origin
                        }


                        mapViewModel.getDirections(origin, route.destination, mode, transit, key)

                        mapViewModel.directions.observe(viewLifecycleOwner) { result ->
                            try {
                                if (result != null) {
                                    if (result.routes!!.isNotEmpty()) {
                                        comparison = (mapViewModel.checkLine == result.routes[0].overviewPolyline.points)
                                        if (!comparison) {
                                            val points = result.routes[0].overviewPolyline.points
                                            val distance = result.routes[0].legs[0].distance.text
                                            val duration = result.routes[0].legs[0].duration.text

                                            val originName = result.routes[0].legs[0].startAddress
                                            val originLatLng = LatLng(result.routes[0].legs[0].startLocation!!.lat, result.routes[0].legs[0].startLocation!!.lng)
                                            val destinationLatLng = LatLng(result.routes[0].legs[0].endLocation!!.lat, result.routes[0].legs[0].endLocation!!.lng)
                                            val destinationName = result.routes[0].legs[0].endAddress
                                            showMarkerOnChoosePlace(originName!!,originLatLng, 0)
                                            showMarkerOnChoosePlace(destinationName!!,destinationLatLng, 0)
                                            showRouteOnMap(points, distance, duration,  mapViewModel.iconType.value!!, 0)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("MYTEST", "ERROR : ${e.localizedMessage}")
                                Log.e("MYTEST", "ERROR : ${e.message}")
                            }
                        }
                    }

                }

                binding.backToProfileBtn.setOnClickListener {
                    val action = PlanMapFragmentDirections.actionPlanMapFragmentToProfileFragment2()
                    view.findNavController().navigate(action)
                }
            }

            if ((navigationArgs.id == 0L).and(navigationArgs.shared.isBlank()).and(navigationArgs.flag.isBlank())) {
                standardBottomSheetBehavior.apply {
                    peekHeight = 120
                    this.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, listFields)
                    .build(requireContext())


                searchView = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
                searchView.setPlaceFields(listFields)
                searchView.setActivityMode(AutocompleteActivityMode.FULLSCREEN)


                binding.myLocationInput.focusable = View.NOT_FOCUSABLE

                searchView.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                    override fun onPlaceSelected(place: Place) {
                        if (binding.myLocationInput.text.toString().isNotBlank()) {
                            val marker = mapViewModel.markers.getOrNull(0)
                            marker?.remove()
                            mapViewModel.markers.removeAt(0)
                            mapViewModel.destinationsName.removeAt(0)
                        }
                        googleMap.apply {
                            val marker = addMarker(
                                MarkerOptions().position(place.latLng!!).title(place.name!!).icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        Random().nextInt(360).toFloat()
                                    )
                                )
                            )
                            mapViewModel.markers.add(0, marker!!)
                            mapViewModel.location = place.latLng!!
                            mapViewModel.destinationsName.add(0, place.name!!)
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
                            } else {
                                myLocationInput.setText(place.name)
                            }
                        }

                    }



                    override fun onError(status: Status) {
                        Log.i("Place", "An error occurred: $status")
                        Toast.makeText(context,"SEARCHING WAS CANCELED", Toast.LENGTH_SHORT).show()
                    }
                })



                recyclerViewSteps.layoutManager = LinearLayoutManager(context)
                stepsAdapter = StepsAdapter()

                imageAdapter = ImageAdapter()
                recyclerViewImage.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                recyclerView.layoutManager = LinearLayoutManager(context)
                inputAdapter = InputAdapter(
                    binding.root,
                    requireContext(),
                    stepsAdapter,
                    recyclerViewSteps,
                    imageAdapter,
                    recyclerViewImage,
                    "",
                    "",
                    mapViewModel,
                    standardBottomSheetBehavior,
                    places,
                    resultLauncher
                )
                recyclerView.adapter = inputAdapter

                val layoutView = layoutInflater.inflate(R.layout.destination_item, null)
                val layout: LinearLayout = layoutView.findViewById(R.id.layout_for_add_stop)

                binding.apply {
                    showInfoMarkersFromChips()
                    startPlanJourney.setOnClickListener {
                        startPlanJourney.visibility = View.GONE
                        chipsInfoMarkersWrapper.visibility = View.GONE
                        addDestination.visibility = View.VISIBLE
                        cancelPlan.visibility = View.VISIBLE
                        planWrapper.visibility = View.VISIBLE
                        if (mapViewModel.poiMarkers.isNotEmpty()) {
                            mapViewModel.poiMarkers.map { marker ->
                                marker.remove()
                            }
                        }
                    }
                    findUserLocation.setOnClickListener {
                        showUserLocation()
                    }
                    cancelPlan.setOnClickListener {
                        addDestination.visibility = View.GONE
                        planWrapper.visibility = View.GONE
                        cancelPlan.visibility = View.GONE
                        startPlanJourney.visibility = View.VISIBLE
                        chipsInfoMarkersWrapper.visibility = View.VISIBLE
                    }
                }

                binding.addDestination.setOnClickListener {
                    binding.searchWrapper.visibility = View.GONE
                    inputAdapter.setName("", "")
                    binding.chipGroupDirections.visibility = View.GONE
                    mapViewModel.notes.add("")
                    mapViewModel.setIconType(mapViewModel.iconType.value!!)
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

                val nameDialog = journeyNameDialog(
                    requireActivity(),
                    mapViewModel,
                    profileViewModel,
                    inputAdapter.getAllDestinations(),
                    binding.root,
                    auth,
                    ref,
                    requireContext()
                )
                binding.finishButton.setOnClickListener {
                    nameDialog.show()
                }

            }
        }
        catch(e: NullPointerException) {
            Log.e("MYTEST", "NullPointerException : $e")

            Log.e("MYTEST", "NullPointerException : ${e.stackTrace}")
            Log.e("MYTEST","NullPointerException : ${e.localizedMessage}")
        }
        catch (e: OutOfMemoryError) {
            Log.e("MYTEST", "OutOfMemoryError : $e")

            Log.e("MYTEST", "OutOfMemoryError : ${e.stackTrace}")
            Log.e("MYTEST","OutOfMemoryError : ${e.localizedMessage}")
        }
        catch (e: IllegalStateException) {
            Log.e("MYTEST", "IllegalStateException : $e")

            Log.e("MYTEST", "IllegalStateException : ${e.stackTrace}")
            Log.e("MYTEST","IllegalStateException : ${e.localizedMessage}")
        }
        catch (e: Exception) {
            Log.e("MYTEST", "Exception : $e")

            Log.e("MYTEST", "Exception : ${e.stackTrace}")
            Log.e("MYTEST","Exception : ${e.localizedMessage}")
        }
    }


    private fun showMarkerOnChoosePlace(locationName: String, coordinates: LatLng, position: Int) {
        val marker = googleMap.addMarker(MarkerOptions().position(coordinates).title(locationName).icon(
            BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())
        ))


        if ((navigationArgs.id == 0L).and(navigationArgs.shared.isBlank()).and(navigationArgs.flag.isBlank())) {
            mapViewModel.markers.add(position,marker!!)
        }

        standardBottomSheetBehavior.peekHeight = 120
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                coordinates,
                15F
            )
        )

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mapG: GoogleMap) {
        googleMap = mapG
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.aubergine
                )
            )
            if (!success) {
                Log.e("MYTEST", "Style parsing failed.")
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MYTEST", "Can't find style. Error: ", e)
        }
        val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(requireContext(),auth.currentUser!!.uid)
        if ((navigationArgs.id == 0L).and(navigationArgs.shared.isBlank()).and(navigationArgs.flag.isBlank())) {
            if (userLoc!!.isEmpty().or(userLoc.isBlank())) {
                if (checkPermissions(requireContext())) {
                    if (isLocationEnabled(requireContext())) {
                        fusedLocationProviderClient.getCurrentLocation(
                            CurrentLocationRequest.Builder().setDurationMillis(30000)
                                .setMaxUpdateAgeMillis(60000).build(), null
                        ).addOnSuccessListener {
                            it?.let {
                                googleMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(it.latitude, it.longitude),
                                        15F
                                    )
                                )
                                val geocoder = Geocoder(requireContext(), Locale.getDefault())

                                try {
                                    val addresses: List<Address>? = geocoder.getFromLocation(
                                        it.latitude, it.longitude, 1
                                    )
                                    if (addresses != null && addresses.isNotEmpty()) {
                                        val cityName = addresses[0].getAddressLine(0)
                                        mapViewModel.destinationsName.add(0, cityName)
                                        val marker = googleMap.addMarker(
                                            MarkerOptions()
                                                .position(LatLng(it.latitude, it.longitude))
                                                .title(cityName)
                                        )
                                        googleMap.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(it.latitude, it.longitude),
                                                15F
                                            )
                                        )
                                        mapViewModel.markers.add(0, marker!!)
                                        binding.myLocationInput.setText(cityName)
                                    }

                                } catch (e: Exception) {
                                    Log.e("MYTEST", "Error: ${e.message}")
                                }

                                googleMap.uiSettings.isMyLocationButtonEnabled = true
                                googleMap.uiSettings.isZoomControlsEnabled = true
                                googleMap.setOnPoiClickListener(this)
                            }
                        }
                    }
                } else {
                    mapViewModel.location = mapViewModel.defaultLocation
                    mapViewModel.destinationsName.add(0, mapViewModel.defaultLocationName)
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(mapViewModel.defaultLocation)
                            .title(mapViewModel.defaultLocationName)
                    )
                    mapViewModel.markers.add(0, marker!!)
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            mapViewModel.defaultLocation,
                            15F
                        )
                    )
                    binding.myLocationInput.setText(mapViewModel.defaultLocationName)
                    googleMap.uiSettings.isMyLocationButtonEnabled = true
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.setOnPoiClickListener(this)
                }
            } else {
                val name = userLoc.split("=")[0]
                val tmpLoc = userLoc.split("=")[1]
                val latLng =
                    LatLng(tmpLoc.split(",")[0].toDouble(), tmpLoc.split(",")[1].toDouble())
                mapViewModel.destinationsName.add(0, name)
                val marker = googleMap.addMarker(
                    MarkerOptions().position(latLng).title(name)
                )
                mapViewModel.markers.add(0, marker!!)
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        15F
                    )
                )
                binding.myLocationInput.setText(name)
                googleMap.uiSettings.isMyLocationButtonEnabled = true
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.setOnPoiClickListener(this)
            }
        }
        else {
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setOnPoiClickListener(this)
        }


    }


    override fun onPoiClick(poi: PointOfInterest) {
        Toast.makeText(context, "Clicked: ${poi.name}", Toast.LENGTH_SHORT).show()
    }

    private fun showUserLocation() {
        val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(requireContext(), auth.currentUser!!.uid)
        if (binding.myLocationInput.text!!.isBlank().or(binding.myLocationInput.text!!.isEmpty())) {
            if (mapViewModel.locationName.isNotBlank().or(mapViewModel.locationName.isNotEmpty())) {
                if (mapViewModel.location != LatLng(0.0,0.0)) {
                    mapViewModel.destinationsName.add(0,mapViewModel.locationName)
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(mapViewModel.location)
                            .title(mapViewModel.locationName)
                    )
                    mapViewModel.markers.add(0,marker!!)
                    binding.myLocationInput.setText(mapViewModel.locationName)
                }
                else {
                    if (userLoc!!.isNotEmpty()) {
                        val tmpLoc = userLoc.split("=")[1]
                        val latLng = LatLng(tmpLoc.split(",")[0].toDouble(), tmpLoc.split(",")[1].toDouble())
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(userLoc.split("=")[0])
                        )
                        if (binding.myLocationInput.text!!.isBlank().or(binding.myLocationInput.text!!.isEmpty())) {
                            if (mapViewModel.markers.isEmpty()) {
                                mapViewModel.markers.add(0,marker!!)
                            }
                            mapViewModel.destinationsName.add(0,userLoc.split("=")[0])
                            binding.myLocationInput.setText(userLoc.split("=")[0])
                        }

                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                15F
                            ))
                    }
                }
            }
        }

        if (mapViewModel.location != LatLng(0.0,0.0)) {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    mapViewModel.location,
                    15F
                ))
        }
        else {
            if (userLoc!!.isNotEmpty()) {
                val tmpLoc = userLoc.split("=")[1]
                val latLng = LatLng(tmpLoc.split(",")[0].toDouble(), tmpLoc.split(",")[1].toDouble())
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(userLoc.split("=")[0])
                )
                if (binding.myLocationInput.text!!.isBlank().or(binding.myLocationInput.text!!.isEmpty())) {
                    if (mapViewModel.markers.isEmpty()) {
                        mapViewModel.markers.add(0,marker!!)
                    }

                    binding.myLocationInput.setText(userLoc.split("=")[0])
                }

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        15F
                    ))
            }
            else {
                getLocation(requireContext(), fusedLocationProviderClient, mapViewModel, auth.currentUser!!.uid)
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        mapViewModel.defaultLocation,
                        15F
                    ))
            }
        }

    }

    private fun showRouteOnMap(line: String, distanceText: String, durationText: String, choosedIcon: String, position: Int) {
        val color = when(choosedIcon){
            "driving" -> Color.WHITE
            "bus" -> Color.rgb(210, 152, 121)
            "train" -> Color.rgb(187, 62, 146)
            "walking" -> Color.rgb(156, 222, 161)
            "bicycling" -> Color.rgb(221,152,184)
            else -> Color.BLUE
        }

        if ((navigationArgs.id == 0L).and(navigationArgs.shared.isBlank()).and(navigationArgs.flag.isBlank())) {
            mapViewModel.setLine(line)
            if (line.isNotBlank()) {
                val polyline: List<LatLng> = PolyUtil.decode(line)

                val options = PolylineOptions()
                options.width(10F)
                options.color(color)
                options.geodesic(true)
                options.addAll(polyline)
                val addedPolyline = googleMap.addPolyline(options)
                addedPolyline.addInfoWindow(googleMap,distanceText,durationText,choosedIcon)

                mapViewModel.polylines.add(addedPolyline)


                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        mapViewModel.markers[position.plus(1)].position,
                        10F
                    )
                )
                calculateDistanceAndDuration(mapViewModel.infoMarkers, binding.root)
            }
        }
        else {
            mapViewModel.setLine(line)
            val polyline: List<LatLng> = PolyUtil.decode(line)

            val options = PolylineOptions()
            options.width(10F)
            options.color(color)
            options.addAll(polyline)
            val addedPolyline = googleMap.addPolyline(options)
            addedPolyline.addInfoWindow(googleMap,distanceText,durationText,choosedIcon)

        }

    }


    private fun showInfoMarkersFromChips() {
        binding.chipGroupMarkers.setOnCheckedStateChangeListener { group, checkedIds ->
            checkedIds.map {
                val chip: Chip? = group.findViewById(it)
                var type = ""
                var radius = 10000
                val locationUser = if (mapViewModel.location != LatLng(0.0,0.0)) {
                    mapViewModel.location
                } else {
                    mapViewModel.defaultLocation
                }
                when (chip?.tag) {
                    "station" -> {
                        type = "gas_station"
                        radius = 20000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                    "hotels" -> {
                        type = "lodging"
                        radius = 10000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                    "restaurants" -> {
                        type = "restaurant"
                        radius = 10000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                    "bar" -> {
                        type = "bar"
                        radius = 10000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                    "hospital" -> {
                        type = "hospital"
                        radius = 30000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                    "store" -> {
                        type = "store"
                        radius = 10000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                    "cafe" -> {
                        type = "cafe"
                        radius = 10000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                    "tourist_attraction" -> {
                        type = "tourist_attraction"
                        radius = 50000
                        mapViewModel.poiMarkers.map { marker ->
                            marker.remove()
                        }
                    }
                }
                val iconMarker = when(type){
                    "gas_station" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_local_gas_station_24)
                    "lodging" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_hotel_24)
                    "restaurant" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_restaurant_24)
                    "bar" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_local_bar_24)
                    "hospital" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_local_hospital_24)
                    "store" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_store_24)
                    "cafe" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_local_cafe_24)
                    "tourist_attraction" -> bitmapDescriptorFromVector(R.drawable.ic_baseline_tour_24)
                    else -> {BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)}
                }

                if (type.isNotBlank()){
                    mapViewModel.getPlacesTypes(locationUser,
                        radius,
                        type,
                        BuildConfig.GOOGLE_MAPS_API_KEY,
                        successCallback = { data ->
                            mapViewModel.placesTypes = data
                            data.forEach {place ->
                                val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                                val info: Marker? = googleMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(place.name)
                                        .snippet(place.vicinity)
                                        .alpha(1f)
                                        .icon(iconMarker)
                                        .anchor(0f, 0f)
                                )
                                info?.showInfoWindow()
                                mapViewModel.poiMarkers.add(info!!)
                            }
                        },
                        errorCallback = { error ->
                            Log.e("MYTEST", "ERROR : $error")
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
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

        if ((navigationArgs.id == 0L).and(navigationArgs.shared.isBlank()).and(navigationArgs.flag.isBlank())) {
            mapViewModel.infoMarkers.add(info!!)
        }
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

