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
import androidx.navigation.fragment.findNavController
import com.example.journey_dp.R

import com.example.journey_dp.databinding.FragmentProfileBinding
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter
import com.example.journey_dp.ui.adapter.events.JourneyEventListener
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.Injection
import kotlinx.coroutines.launch
import kotlin.math.round


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
                    profileViewModel.showMapFlag = false
                    val action = ProfileFragmentDirections.actionProfileFragment2ToPlanMapFragment(
                        id = 0L
                    )
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

        journeysAdapter = JourneysAdapter(
            journeyEventListener = JourneyEventListener { journeyId: Long ->
                Log.i("MYTEST", "CLICKED: $journeyId")
                profileViewModel.showMapFlag = true
                findNavController()
                    .navigate(
                        ProfileFragmentDirections.actionProfileFragment2ToPlanMapFragment(
                            id = journeyId
                        )
                    )
            }
        )
        binding.journeysListRecyclerview.adapter = journeysAdapter



        profileViewModel.viewModelScope.launch {
            profileViewModel.journeysWithDestinations.observe(viewLifecycleOwner) {
                binding.numberOfJourneys.text = getString(R.string.total_destinations).plus(" ${it.size}")
                var distance = 0.0
                var durationHours = 0
                var durationMinutes = 0
                var days = 0
                it.map { item ->
                    val tmp = item.journey.totalDistance.split("km")[0].split(":")[1].trim()
                    distance += ((tmp.split(",")[0]).plus('.').plus(tmp.split(",")[1])).toDouble()
                    val hours = item.journey.totalDuration.split(" h")[0].split(":")[1].trim().toInt()
                    durationHours += hours
                    val minutes = item.journey.totalDuration.split("h")[1].split("m")[0].trim().toInt()
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

                val distanceDetails = getString(R.string.total_distance).plus(" ${round(distance)}").plus(" km")
                val durationDetails = getString(R.string.days).plus(" $days").plus(" ").plus(getString(R.string.hours)).plus(" $durationHours  ").plus(getString(R.string.minutes)).plus("  $durationMinutes")
                binding.calculatedDistance.text = distanceDetails
                binding.calculatedDuration.text = durationDetails

            }

        }


    }

}