package com.example.journey_dp.utils


import android.app.Dialog
import android.content.Context

import android.content.Intent

import android.graphics.Color
import android.net.Uri

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.example.journey_dp.R

import com.example.journey_dp.ui.fragments.journey.PlanJourneyFragmentDirections
import com.example.journey_dp.ui.fragments.journey.ProfileFragmentDirections
import com.example.journey_dp.ui.fragments.maps.TestMapFragmentDirections
import com.example.journey_dp.ui.viewmodel.MapViewModel

import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

import org.jsoup.Jsoup
import java.net.URLDecoder
import java.util.*


fun isLightColor(color: Int): Boolean {
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)
    // Calculate the luminance of the color
    val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
    // Check if the luminance is greater than 0.5 (i.e., light color)
    return luminance > 0.5
}

fun clearFromHtml(htmlString: String): String {
    val decoded = URLDecoder.decode(htmlString, "UTF-8")
    return Jsoup.parse(decoded).text()
}

fun setLogOut(
    activity: FragmentActivity,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    view: View
) {
    // The usage of an interface lets you inject your own implementation
    val menuHost: MenuHost = activity

    // Add menu items without using the Fragment Menu APIs
    // Note how we can tie the MenuProvider to the viewLifecycleOwner
    // and an optional Lifecycle.State (here, RESUMED) to indicate when
    // the menu should be visible
    menuHost.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            // Add menu items here
            menuInflater.inflate(R.menu.log_out, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            // Handle the menu selection
            return when (menuItem.itemId) {
                R.id.action_logout -> {
                    logOurDialog(activity, view, context)
                    true
                }
                else -> false
            }
        }
    }, lifecycleOwner, Lifecycle.State.RESUMED)
}

fun hideElements(view: View) {
    val chips = view.findViewById<ChipGroup>(R.id.chip_group_directions)
    val steps = view.findViewById<ScrollView>(R.id.steps_scrollView)
    steps.visibility = View.GONE
    chips.visibility = View.GONE

}

fun calculateDistanceAndDuration(infoMarkers: MutableList<Marker>, view: View) {
    var totalDistance = 0.0
    var totalDurationHours = 0
    var totalDurationMinutes = 0
    val helperDistanceText = "Total Distance : "
    val helperDurationText = "Total Duration : "
    infoMarkers.map {
        val distanceItem = it.title!!.split("km")[0].trim().toDouble()
        var durationItemHour = 0
        var durationItemMinutes = ""
        if (it.snippet!!.contains("hour")) {
            durationItemHour = it.snippet!!.split("hour")[0].trim().toInt()
            durationItemMinutes = it.snippet!!.split("hour")[1].split("mins")[0]
            if (durationItemMinutes.contains("s")) {
                durationItemMinutes = durationItemMinutes.split("s")[1]
            }
            totalDurationHours += durationItemHour
            totalDurationMinutes += durationItemMinutes.trim().toInt()
        }
        else {
            durationItemMinutes = it.snippet!!.split("mins")[0]
            totalDurationMinutes += durationItemMinutes.trim().toInt()
        }
        totalDistance += distanceItem
        if (totalDurationMinutes >= 60) {
            val h = totalDurationMinutes.div(60)
            val m = totalDurationMinutes.mod(60)
            totalDurationHours += h
            totalDurationMinutes = m
        }

        Log.i("MYTEST", "___________________________________")
        Log.i("MYTEST", "DISTANCE : $distanceItem")
        Log.i("MYTEST", "DURATION : $durationItemHour h  $durationItemMinutes m")
        Log.i("MYTEST", "___________________________________")
    }
    Log.i("MYTEST", "___________________________________")
    Log.i("MYTEST", "TOTAL DISTANCE : $totalDistance")
    Log.i("MYTEST", "TOTAL DURATION : $totalDurationHours h  $totalDurationMinutes m")
    val distanceView = view.findViewById<TextView>(R.id.totalDistance)
    val durationView = view.findViewById<TextView>(R.id.totalDuration)
    distanceView.text = helperDistanceText.plus(String.format("%.2f", totalDistance)).plus(" km")
    durationView.text = helperDurationText.plus(totalDurationHours.toString()).plus( " h ").plus(totalDurationMinutes.toString()).plus(" m")

}


fun setMapMenu(
    activity: FragmentActivity,
    lifecycleOwner: LifecycleOwner,
    view: View
) {
    val menuHost: MenuHost = activity
    menuHost.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.top_map_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.home -> {
                    val action = TestMapFragmentDirections.actionTestMapFragmentToPlanJourneyFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.profile -> {
                    val action = TestMapFragmentDirections.actionTestMapFragmentToProfileFragment2()
                    view.findNavController().navigate(action)
                    true
                }
                else -> false
            }


        }
    }, lifecycleOwner, Lifecycle.State.RESUMED)
}




fun callIntent(uriWeb: String, context: Context) {
    if (uriWeb.startsWith("https://") || uriWeb.startsWith("http://")) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uriWeb))
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show()
    }
}

fun showWebPageIntent(uriWeb: String, context: Context) {
    if (uriWeb.startsWith("https://") || uriWeb.startsWith("http://")) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriWeb))
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show()
    }
}


fun journeyNameDialog(activity: FragmentActivity, model: MapViewModel, allDestinations: MutableList<String>, mapView: View): Dialog {
    return activity.let {
        val builder = AlertDialog.Builder(it)
        val inflater = activity.layoutInflater;


        val totalDistance = mapView.findViewById<TextView>(R.id.totalDistance)
        val totalDuration = mapView.findViewById<TextView>(R.id.totalDuration)

        val view = inflater.inflate(R.layout.journey_name_dialog, null)
        val journeyName = view.findViewById<TextInputEditText>(R.id.journey_name)


        builder.setView(view)
            .setPositiveButton(R.string.confirm
            ) { dialog, id ->
                val journeyGeneratedName = "Journey-" + Random().nextInt(1000).toString()
                if (journeyName.text.toString().isBlank()) {
                    journeyName.setText(journeyGeneratedName)
                }
                if (allDestinations.isEmpty().and(model.travelMode.isEmpty()).and(model.notes.isEmpty())) {
                    dialog.cancel()
                }
                else {
                    allDestinations.removeAll {destination -> destination.isBlank() }
                    model.travelMode.removeAll { travel -> travel.isBlank() }
                    model.notes.removeAll { note -> note.isBlank() }
                    Log.i("MYTEST", "-----------------------------------------------")
                    Log.i("MYTEST", "NAME WAS ADDED : ${journeyName.text.toString()}")
                    Log.i("MYTEST", "FIRST ORIGIN : ${model.location.value.toString()}")
                    Log.i("MYTEST", "TOTAL DISTANCE : ${totalDistance.text}")
                    Log.i("MYTEST", "TOTAL DURATION : ${totalDuration.text}")
                    Log.i("MYTEST", "ALL DESTINATIONS BEFORE CLEAR: $allDestinations")
                    Log.i("MYTEST", "ALL TRAVEL MODES BEFORE CLEAR ${model.travelMode}")
                    Log.i("MYTEST", "ALL NOTES CLEAR : ${model.notes}")
                    Log.i("MYTEST", "-----------------------------------------------")
                }
            }
            .setNegativeButton(R.string.cancel
            ) { dialog, id ->
                Log.i("MYTEST", "DIALOGA WAS CANCELED : ${dialog.cancel()}")
                dialog.cancel()
            }
        builder.create()
    }
}

fun saveJourney() {
    // TODO: will be implemented
}



fun logOurDialog(activity: FragmentActivity,view: View, context: Context) {
    val alertDialog: AlertDialog = activity.let {
        val builder = AlertDialog.Builder(it)
        builder.apply {
            setTitle("Are you sure to log out ? ")
            setMessage("Good bye :(")
            setPositiveButton("OK"
            ) { dialog, id ->
                view.findNavController().navigate(
                    PlanJourneyFragmentDirections.actionPlanJourneyFragmentToLoginFragment()
                )
            }
            setNegativeButton("Cancel"
            ) { dialog, id ->
                dialog.cancel()
            }
        }
        // Create the AlertDialog
        builder.create()
    }
    alertDialog.show()
}


//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        when {
//            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                // Precise location access granted.
//                getLocation(requireContext())
//            }
//            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                // Only approximate location access granted.
//                getLocation(requireContext())
//            }
//            else -> {
//            // No location access granted.
//                permissionStateDenied = true
//
//            }
//        }
//    }

// Check if location is enabled or not and return boolean
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }

//    private fun checkPermissions(context: Context): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun requestPermissions() {
//        locationPermissionRequest.launch(arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION))
//    }


//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    @SuppressLint("MissingPermission")
//    fun getLocation(context: Context){
//        if (checkPermissions(context)) {
//                if (isLocationEnabled()) {
//                    fusedLocationProviderClient.getCurrentLocation(
//                        CurrentLocationRequest.Builder().setDurationMillis(30000)
//                            .setMaxUpdateAgeMillis(60000).build(), null
//                    ).addOnSuccessListener {
//                        it?.let {
//                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
//
//
//                            // DEPRECATED FOR TIRAMISU VERSION
//                            val addresses: List<Address>? = geocoder.getFromLocation(it.latitude, it.longitude,1)
//                            val cityName: String = addresses!![0].getAddressLine(0)
////                            lifecycleScope.launch {
////                                geocoder.getFromLocation(it.latitude, it.longitude, 1) {addresses->
////                                    cityName = addresses[0].getAddressLine(0)
////                                }
////                            }
//
//                            googleMap.clear()
//                            markers.removeAt(0)
//                            val marker = googleMap.addMarker(
//                                MarkerOptions()
//                                    .position(LatLng(it.latitude, it.longitude))
//                                    .title(cityName)
//                            )
//
//                            markers.add(0,marker!!)
//                            googleMap.animateCamera(
//                                CameraUpdateFactory.newLatLngZoom(
//                                    LatLng(it.latitude, it.longitude),
//                                    15F
//                                ))
//                            binding.myLocationInput.setText(cityName)
//                        }
//                    }
//                }
//            else {
//                Toast.makeText(context,"Please turn on location", Toast.LENGTH_SHORT).show()
//            }
//        }
//        else {
//            requestPermissions()
//        }
//    }










// Odlozeny kod
//                    val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
//                    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
//                    val placeResponse = places.findCurrentPlace(request)
//                    placeResponse.addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val response = task.result
//                            for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
//                                googleMap.addMarker(
//                                MarkerOptions()
//                                    .position(LatLng(placeLikelihood.place.latLng!!.latitude,placeLikelihood.place.latLng!!.longitude))
//                                    .title(placeLikelihood.place.name)
//                            )
//
//                            googleMap.animateCamera(
//                                CameraUpdateFactory.newLatLngZoom(
//                                    LatLng(placeLikelihood.place.latLng!!.latitude,placeLikelihood.place.latLng!!.longitude),
//                                    15F
//                                ))
//                            }
//                        } else {
//                            val exception = task.exception
//                            if (exception is ApiException) {
//                                Log.e("PlacesError", "Place not found: ${exception.statusCode}")
//                            }
//                        }
//                    }