package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.journey_dp.R

import com.example.journey_dp.databinding.FragmentProfileBinding



class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

}