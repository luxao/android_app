package com.example.journey_dp.utils


import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.example.journey_dp.R
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.RouteEntity
import com.example.journey_dp.ui.fragments.journey.PlanJourneyFragmentDirections
import com.example.journey_dp.ui.fragments.maps.PlanMapFragmentDirections
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.shared.LocationSharedPreferencesUtil
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.jsoup.Jsoup
import java.net.URLDecoder
import java.util.*




fun clearFromHtml(htmlString: String): String {
    val decoded = URLDecoder.decode(htmlString, "UTF-8")
    return Jsoup.parse(decoded).text()
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
    val helperDistanceText = "Journey Distance : "
    val helperDurationText = "Journey Duration : "
    infoMarkers.map {
        val distanceItem = it.title!!.split("km")[0].trim().toDouble()
        var durationItemHour = 0
        var durationItemMinutes = ""

        if (it.snippet!!.contains("day").or(it.snippet!!.contains("days"))) {
            val tmp = it.snippet!!.split(" ")
            totalDurationHours += tmp[0].toInt().times(24)
            if (tmp.size == 4) {
                if ((tmp[3] == "hours").or(tmp[3] == "hour")) {
                    totalDurationHours += tmp[2].toInt()
                }
                if ((tmp[3] == "mins").or(tmp[3] == "min")) {
                    totalDurationMinutes += tmp[2].toInt()
                }
            }
            if (tmp.size > 4) {
                totalDurationHours += tmp[2].toInt()
                totalDurationMinutes += tmp[4].toInt()
            }
        }

        else if (it.snippet!!.contains("hour")) {
            durationItemHour = it.snippet!!.split("hour")[0].trim().toInt()
            durationItemMinutes = if (it.snippet!!.contains("min")) {
                it.snippet!!.split("hour")[1].split("min")[0]
            } else {
                it.snippet!!.split("hour")[1].split("mins")[0]
            }

            if (durationItemMinutes.contains("s")) {
                durationItemMinutes = durationItemMinutes.split("s")[1]
            }
            totalDurationHours += durationItemHour
            totalDurationMinutes += durationItemMinutes.trim().toInt()
        }
        else {
            durationItemMinutes = if (it.snippet!!.contains("min")) {
                it.snippet!!.split("min")[0]
            } else {
                it.snippet!!.split("mins")[0]
            }

            totalDurationMinutes += durationItemMinutes.trim().toInt()
        }
        totalDistance += distanceItem
        if (totalDurationMinutes >= 60) {
            val h = totalDurationMinutes.div(60)
            val m = totalDurationMinutes.mod(60)
            totalDurationHours += h
            totalDurationMinutes = m
        }
    }
    val distanceView = view.findViewById<TextView>(R.id.totalDistance)
    val durationView = view.findViewById<TextView>(R.id.totalDuration)
    distanceView.text = helperDistanceText.plus(String.format("%.2f", totalDistance)).plus(" km")
    durationView.text = helperDurationText.plus(totalDurationHours.toString()).plus( " h ").plus(totalDurationMinutes.toString()).plus(" m")

}




var currentChar = 'A'

fun setAgainCharacter() {
    currentChar = 'A'
}
fun nextABC(): Char {
    val result = currentChar
    currentChar = currentChar.plus(1)
    return result
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


fun journeyNameDialog(activity: FragmentActivity, model: MapViewModel,
                      profileViewModel: ProfileViewModel,
                      allDestinations: MutableList<String>, mapView: View, auth: FirebaseAuth,firebaseDatabase: DatabaseReference, context: Context): Dialog {
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
                if (allDestinations.isEmpty().and(model.polylines.isEmpty()).and(model.notes.isEmpty())) {
                    Toast.makeText(activity.applicationContext, "YOU CANNOT CREATE EMPTY JOURNEY", Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                }
                else {
                    if (model.polylines.isNotEmpty()) {
                        allDestinations.removeAll {destination -> destination.isBlank() }
                        model.travelMode.removeAll { travel -> travel.isBlank() }

                        val parsedOrigin = if (model.location != LatLng(0.0,0.0)) {
                            model.location.latitude.toString().plus(",${model.location.longitude.toString()}")
                        } else {
                            val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(context,auth.currentUser!!.uid)
                            val tmpLoc = userLoc!!.split("=")[1]
                            tmpLoc.split(",")[0] + "," + tmpLoc.split(",")[1]
                        }

                        allDestinations.add(0,parsedOrigin)
                        val userEmail = auth.currentUser!!.email
                        val userUid = auth.currentUser!!.uid
                        val journey = JourneyEntity(
                            user = userEmail.toString(),
                            name = journeyName.text.toString(),
                            totalDistance = totalDistance.text.toString(),
                            totalDuration = totalDuration.text.toString(),
                            numberOfDestinations = allDestinations.size,
                            sharedUrl = ""
                        )
                        val routes = mutableListOf<RouteEntity>()
                        var buildUrl = "https://planjourney/map?details="
                        for (item in 0 until allDestinations.size.minus(1)) {
                            if (item != allDestinations.size.minus(1)) {
                                val route = RouteEntity(
                                    journeyId = journey.id,
                                    origin = allDestinations[item],
                                    destination = allDestinations[item.plus(1)],
                                    travelMode = model.travelMode[item],
                                    note = if (model.notes.isEmpty()) "" else model.notes[item],
                                    originName = model.destinationsName[item],
                                    destinationName = model.destinationsName[item.plus(1)]
                                )
                                buildUrl += allDestinations[item].plus("|${allDestinations[item.plus(1)].plus("|${model.travelMode[item]}_")}")
                                routes.add(route)
                            }
                        }
                        journey.sharedUrl = buildUrl.dropLast(1)
                        profileViewModel.insertJourneyWithDestinations(journey, routes)

                        firebaseDatabase.child("users_journeys").child(userUid).child(journey.name).setValue(journey)
                        firebaseDatabase.child("users_journeys").child(userUid).child(journey.name).child("routes").setValue(routes)

                        val action = PlanMapFragmentDirections.actionPlanMapFragmentToProfileFragment2()
                        mapView.findNavController().navigate(action)
                    }
                    else {
                        Toast.makeText(activity.applicationContext, "YOU CANNOT CREATE EMPTY JOURNEY", Toast.LENGTH_SHORT).show()
                        dialog.cancel()
                    }

                }
            }
            .setNegativeButton(R.string.cancel
            ) { dialog, id ->
                dialog.cancel()
            }
        builder.create()
    }
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

// Check if location is enabled or not and return boolean
fun isLocationEnabled(context: Context): Boolean {
    val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}


fun checkPermissions(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}



@SuppressLint("MissingPermission")
fun getLocation(context: Context, fusedLocationProviderClient: FusedLocationProviderClient, model: MapViewModel, userID: String){
    if (checkPermissions(context)) {
        if (isLocationEnabled(context)) {
            fusedLocationProviderClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setDurationMillis(30000)
                    .setMaxUpdateAgeMillis(60000).build(), null
            ).addOnSuccessListener {
                it?.let {
                    val geocoder = Geocoder(context, Locale.getDefault())

                    try {
                        if (android.os.Build.VERSION.SDK_INT < 33) {
                            val addresses: List<Address>? = geocoder.getFromLocation(it.latitude, it.longitude,1)

                            val cityName: String = addresses!![0].getAddressLine(0)
                            val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(context, userID)
                            if ((addresses[0].countryCode != null).or(addresses[0].countryCode.isNotBlank()).or(addresses[0].countryCode.isNotEmpty())) {

                            }

                            if ((userLoc!!.isBlank()).or(userLoc.isEmpty())) {
                                LocationSharedPreferencesUtil.getInstance().putUserLoc(context, cityName, LatLng(it.latitude, it.longitude), userID)
                                model.locationName = cityName
                                model.location = LatLng(it.latitude, it.longitude)
                            }
                            else {
                                val compare = userLoc.split("=")[0]
                                if (compare != cityName) {
                                    LocationSharedPreferencesUtil.getInstance().putUserLoc(context, cityName, LatLng(it.latitude, it.longitude), userID)
                                    model.locationName = cityName
                                    model.location = LatLng(it.latitude, it.longitude)

                                }
                            }

                        }
                        else {
                            val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(context, userID)
                            geocoder.getFromLocation(it.latitude, it.longitude, 1) {addresses->
                                val cityName =  addresses[0].getAddressLine(0)
                                if ((addresses[0].countryCode != null).or(addresses[0].countryCode.isNotBlank()).or(addresses[0].countryCode.isNotEmpty())) {
                                    if ((userLoc!!.isBlank()).or(userLoc.isEmpty())) {
                                        LocationSharedPreferencesUtil.getInstance().putUserLoc(context, cityName, LatLng(it.latitude, it.longitude), userID)
                                        model.locationName = cityName
                                        model.location = LatLng(it.latitude, it.longitude)

                                    }

                                    else {
                                        val compare = userLoc.split("=")[0]
                                        if (compare != cityName) {
                                            LocationSharedPreferencesUtil.getInstance().putUserLoc(context, cityName, LatLng(it.latitude, it.longitude), userID)
                                            model.locationName = cityName
                                            model.location = LatLng(it.latitude, it.longitude)

                                        }
                                    }


                                }
                            }
                        }

                    }
                    catch (e: Exception) {
                        Log.e("MYTEST","ERROR : ${e.message}")
                    }

                }
            }
        }
        else {
            Toast.makeText(context,"Please turn on location", Toast.LENGTH_SHORT).show()
        }
    }
}






