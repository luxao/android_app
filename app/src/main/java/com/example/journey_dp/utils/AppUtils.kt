package com.example.journey_dp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.example.journey_dp.R

import com.example.journey_dp.ui.fragments.journey.PlanJourneyFragmentDirections
import com.example.journey_dp.ui.fragments.maps.TestMapFragmentDirections

import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient


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

fun setMapMenu(
    activity: FragmentActivity,
    lifecycleOwner: LifecycleOwner,
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
            menuInflater.inflate(R.menu.top_map_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            // Handle the menu selection
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




fun logOurDialog(activity: FragmentActivity,view: View, context: Context) {
    val alertDialog: AlertDialog = activity.let {
        val builder = AlertDialog.Builder(it)
        builder.apply {
            setTitle("Are you sure to log out ? ")
            setMessage("Good bye :(")
            setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, id ->
                    view.findNavController().navigate(
                        PlanJourneyFragmentDirections.actionPlanJourneyFragmentToLoginFragment()
                    )
                })
            setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
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