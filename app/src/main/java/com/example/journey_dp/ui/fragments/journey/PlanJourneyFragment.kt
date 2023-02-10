package com.example.journey_dp.ui.fragments.journey


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.journey_dp.databinding.FragmentPlanJourneyBinding



import com.example.journey_dp.utils.setLogOut


class PlanJourneyFragment : Fragment() {

    private var _binding : FragmentPlanJourneyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlanJourneyBinding.inflate(inflater, container, false)
        setLogOut(
            context = requireContext(),
            activity = requireActivity() ,
            lifecycleOwner = viewLifecycleOwner,
            view = binding.root
        )
        return binding.root
    }



}