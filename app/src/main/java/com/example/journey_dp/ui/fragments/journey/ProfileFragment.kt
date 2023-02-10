package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController

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


        binding.backHome.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToPlaneJourneyFragment()
            view.findNavController().navigate(action)
        }
    }

}