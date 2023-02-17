package com.example.journey_dp.ui.fragments.maps

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.BuildConfig
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentTestBinding
import com.example.journey_dp.ui.viewmodel.InputViewModel
import com.example.journey_dp.utils.Injection
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText


class TestFragment : Fragment() {

    // Declaration of binding fragment
    private var _binding : FragmentTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var inputViewModel: InputViewModel
    private lateinit var placeFromSearch: Place
    private lateinit var layout: LinearLayout
    private lateinit var places: PlacesClient
    private var isStatusSet: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var status: Status


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let {
                    placeFromSearch = Autocomplete.getPlaceFromIntent(result.data!!)
                    inputViewModel.setPlaceName(placeFromSearch.name!!)
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addInputs()
    }

    private fun addInputs() {
        layout = binding.container
        val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val addInput = layoutInflater.inflate(R.layout.destination_item,  null)


        val test: TextInputEditText = addInput.findViewById(R.id.input_destination)
        val searchIntent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(requireContext())
        val delete: ImageView = addInput.findViewById(R.id.delete_input)

        test.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View?) {
                resultLauncher.launch(searchIntent)
            }
        })

        delete.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View?) {
                layout.removeView(addInput)
            }
        })

        binding.testButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View?) {
                layout.addView(addInput)
            }
        })


    }



}