package com.example.journey_dp.ui.fragments.maps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentShowJourneyBinding
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.setMapMenu
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior



class ShowJourneyFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {
    private var _binding : FragmentShowJourneyBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapViewModel: MapViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var map: GoogleMap

    private lateinit var bottomSheet: BottomSheetBehavior<View>

    private lateinit var places: PlacesClient

    private val navigationArgs: ShowJourneyFragmentArgs by navArgs()


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
        _binding = FragmentShowJourneyBinding.inflate(inflater, container, false)
        setMapMenu(
            activity = requireActivity() ,
            lifecycleOwner = viewLifecycleOwner,
            view = binding.root
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Log.i("MYTEST", "FROM ACTION: ${navigationArgs.id}")
        profileViewModel.journeyId.postValue(navigationArgs.id)

        places = activity?.applicationContext?.let { Places.createClient(it) }!!

        bottomSheet = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheet.apply {
            peekHeight = 80
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        profileViewModel.journeyWithRoutes.observe(viewLifecycleOwner) {
            Log.i("MYTEST", "IT  $it")
            if (it != null) {
                Log.i("MYTEST", "CHOOSED JOURNEY: ${it.journey}")
                Log.i("MYTEST", "CHOOSED ROUTES: ${it.routes}")
            }
        }

        binding.backToProfile.setOnClickListener {
            val action = ShowJourneyFragmentDirections.actionShowJourneyFragmentToProfileFragment2()
            view.findNavController().navigate(action)
        }

    }

    override fun onMapReady(mapG: GoogleMap) {
        map = mapG
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnPoiClickListener(this)
    }


    override fun onPoiClick(poi: PointOfInterest) {
        Toast.makeText(context, "Clicked: ${poi.name}", Toast.LENGTH_SHORT).show()
    }



}