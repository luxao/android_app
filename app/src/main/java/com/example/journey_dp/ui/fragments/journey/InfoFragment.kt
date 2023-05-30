package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.journey_dp.databinding.FragmentInfoBinding


class InfoFragment : Fragment() {
    private var _binding : FragmentInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backToProfile.setOnClickListener {
            val action =
                InfoFragmentDirections.actionInfoFragmentToProfileFragment2()
            view.findNavController().navigate(action)
        }
    }

}