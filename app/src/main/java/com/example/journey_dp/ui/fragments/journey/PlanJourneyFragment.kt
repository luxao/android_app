package com.example.journey_dp.ui.fragments.journey


import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.journey_dp.databinding.FragmentPlanJourneyBinding
import com.example.journey_dp.ui.fragments.auth.LoginFragmentDirections


import com.example.journey_dp.utils.setLogOut
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startPlan.setOnClickListener {
            val action = PlanJourneyFragmentDirections.actionPlanJourneyFragmentToPlanMapFragment(
                id = 0L,
                shared = "",
                flag = ""
            )
            view.findNavController().navigate(action)
        }
    }
}