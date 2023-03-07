package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.journey_dp.R

import com.example.journey_dp.databinding.FragmentProfileBinding
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.Injection
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var journeysAdapter: JourneysAdapter
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[ProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this@ProfileFragment.viewLifecycleOwner
        binding.model = this@ProfileFragment.profileViewModel

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeJourney -> {
                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToPlanJourneyFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.planJourney -> {
                    val action = ProfileFragmentDirections.actionProfileFragment2ToTestMapFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.settings -> {
                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToPlanJourneyFragment()
                    view.findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        journeysAdapter = JourneysAdapter()
        binding.journeysListRecyclerview.adapter = journeysAdapter


        profileViewModel.viewModelScope.launch {
            profileViewModel.journeysWithDestinations.observe(viewLifecycleOwner) {
                binding.numberOfJourneys.text = it.size.toString()
                var distance = 0.0
                var durationHours = 0
                var durationMinutes = 0
                var days = 0
                it.map { item ->
                    val tmp = item.journey.totalDistance.split("km")[0].toFloat()
                    distance += tmp
                    val hours = item.journey.totalDuration.split(" h")[0].toInt()
                    durationHours += hours
                    val minutes = item.journey.totalDuration.split("h")[1].split("m")[0].toInt()
                    durationMinutes += minutes
                }
                if (durationHours >= 24) {
                    days = durationHours.div(24)
                    durationHours = durationMinutes.mod(24)
                }
                if (durationMinutes >= 60) {
                    durationHours += durationMinutes.div(60)
                    durationMinutes = durationMinutes.mod(60)
                }

                Log.i("MYTEST", "DISTANCE ALL : $distance")
                Log.i("MYTEST", "DAYS ALL : $days")
                Log.i("MYTEST", "HOURS ALL : $durationHours")
                Log.i("MYTEST", "MINUTES ALL : $durationMinutes")
            }
        }


    }

}