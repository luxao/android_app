package com.example.journey_dp.ui.fragments.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentSearchBinding
import com.example.journey_dp.ui.viewmodel.SearchViewModel
import com.example.journey_dp.utils.Injection
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class SearchFragment : Fragment() {


    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel

    private lateinit var places: PlacesClient
    private lateinit var searchView: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[SearchViewModel::class.java]

        activity?.applicationContext?.let { Places.initialize(it, BuildConfig.GOOGLE_MAPS_API_KEY) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val search = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        search.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        binding.lifecycleOwner = this@SearchFragment.viewLifecycleOwner
        binding.model = this@SearchFragment.viewModel

//        viewModel.deleteJourneys()
//        viewModel.deleteDestinations()

        searchView = search
        places = activity?.applicationContext?.let { Places.createClient(it) }!!

        searchView.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("Place", "Place: ${place.name}, ${place.id}, ${place.latLng?.latitude}, ${place.latLng?.longitude}")


            }

            override fun onError(status: Status) {
                Log.i("Place", "An error occurred: $status")
                Toast.makeText(context, "Error ! Please Try again $status", Toast.LENGTH_SHORT ).show()
            }

        })


//        binding.backToMap.setOnClickListener {
//                findNavController().navigate(
//                    SearchFragmentDirections.actionSearchFragmentToMapFragment(
//                        location = "Bratislava",
//                        latitude = "48.15",
//                        longitude = "17.5645"
//                    )
//                )
//
//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}