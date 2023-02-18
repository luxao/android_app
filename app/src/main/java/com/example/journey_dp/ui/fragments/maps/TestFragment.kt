package com.example.journey_dp.ui.fragments.maps

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentTestBinding


import com.example.journey_dp.ui.adapter.adapters.InputAdapter

import com.example.journey_dp.ui.viewmodel.InputViewModel
import com.example.journey_dp.utils.Injection
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place

import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity



class TestFragment : Fragment() {

    // Declaration of binding fragment
    private var _binding : FragmentTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputAdapter: InputAdapter

    private lateinit var inputViewModel: InputViewModel
    private lateinit var placeFromSearch: Place

    private var isStatusSet: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var status: Status
    private val inputs = mutableListOf<LinearLayout>()


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let {
                    placeFromSearch = Autocomplete.getPlaceFromIntent(result.data!!)
                    inputViewModel.setPlaceName(placeFromSearch.name!!)
                    Log.i("TEST", "PLACE VALUES: ${placeFromSearch.name}, ${placeFromSearch.id}")

                    Log.i("TEST", "MODEL VALUES: ${inputViewModel.placeName.value}, ${inputViewModel.isPlaceSet.value}")
                    inputAdapter.setName(placeFromSearch.name!!)
                    val position = inputAdapter.getID()

                    Log.i("TEST", "MODEL POSITION: $position")
                    if (position != -1) {
                        inputAdapter.onPlaceSelected(placeFromSearch, position)
                    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.applicationContext?.let { Places.initialize(it, BuildConfig.GOOGLE_MAPS_API_KEY) }

        inputViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[InputViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        recyclerView = binding.inputsList
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        inputAdapter = InputAdapter("",inputs,resultLauncher)
        recyclerView.adapter = inputAdapter
        val layoutView = layoutInflater.inflate(R.layout.destination_item, null)
        val layout: LinearLayout = layoutView.findViewById(R.id.layout_for_add_stop)

        binding.testButton.setOnClickListener {
            inputAdapter.setName("")
            inputs.add(layout)
            inputAdapter.notifyItemInserted(inputs.size)
            inputViewModel.setValue(false)
        }


        inputViewModel.isPlaceSet.observe(viewLifecycleOwner) {
            Log.i("TEST", "MODEL: ${inputViewModel.placeName.value}, ${inputViewModel.isPlaceSet.value}")
        }

    }


}