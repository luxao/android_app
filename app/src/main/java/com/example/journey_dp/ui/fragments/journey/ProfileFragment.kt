package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.journey_dp.R

import com.example.journey_dp.databinding.FragmentProfileBinding
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.Injection


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
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

        profileViewModel.journeysWithDestinations.observe(viewLifecycleOwner) {
            Log.i("MYTEST", "FROM PROFILE : $it")
        }
    }

}