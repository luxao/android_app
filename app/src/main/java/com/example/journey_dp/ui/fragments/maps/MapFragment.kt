package com.example.journey_dp.ui.fragments.maps


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment


class MapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {

   // private val navigationArgs: MapFragmentArgs by navArgs()
    private var _binding : FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var places: PlacesClient
    private lateinit var searchView: AutocompleteSupportFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(mapG: GoogleMap) {
        googleMap = mapG
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng( 48.148598, 17.107748))
                .title("Bratislava")
        )

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng( 48.148598, 17.107748),
            15F
        ))

        googleMap.setOnPoiClickListener(this)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false

//        if (navigationArgs.location!!.isNotBlank() && navigationArgs.latitude!!.isNotBlank() && navigationArgs.longitude!!.isNotBlank()) {
//            Log.i("Search Data", navigationArgs.location.toString())
//            Log.i("Search Data", navigationArgs.latitude.toString())
//            Log.i("Search Data", navigationArgs.longitude.toString())
//
//            googleMap.addMarker(
//                MarkerOptions()
//                    .position(LatLng( navigationArgs.latitude!!.toDouble(), navigationArgs.longitude!!.toDouble()))
//                    .title(navigationArgs.location)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//            )
//
//
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng( navigationArgs.latitude!!.toDouble(), navigationArgs.longitude!!.toDouble()),
//                15F
//            ))
//
//        }

//        binding.searchBtn.setOnClickListener {
//            findNavController().navigate(
//                MapFragmentDirections.actionMapFragmentToSearchFragment()
//            )
//        }

    }

    override fun onPoiClick(poi: PointOfInterest) {
        Toast.makeText(context, """Clicked: ${poi.name}
            Latitude:${poi.latLng.latitude} Longitude:${poi.latLng.longitude}""",
            Toast.LENGTH_SHORT
        ).show()
        googleMap.uiSettings.isMapToolbarEnabled = false
    }


}