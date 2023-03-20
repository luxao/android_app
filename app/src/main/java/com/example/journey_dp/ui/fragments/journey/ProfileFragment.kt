package com.example.journey_dp.ui.fragments.journey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toIcon
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.bumptech.glide.Glide
import com.example.journey_dp.R

import com.example.journey_dp.databinding.FragmentProfileBinding
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter
import com.example.journey_dp.ui.adapter.events.JourneyEventListener
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.JourneyEnum
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.math.round


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var journeysAdapter: JourneysAdapter
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
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


        Glide.with(requireContext()).load(auth.currentUser?.photoUrl).circleCrop().into(binding.profilePicture)
        binding.nameOfUser.text = auth.currentUser?.displayName

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeJourney -> {
                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToPlanJourneyFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.planJourney -> {
                    val action = ProfileFragmentDirections.actionProfileFragment2ToPlanMapFragment(
                        id = 0L,
                        shared = "",
                        flag = ""
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
            context = requireContext(),
            model = profileViewModel,
            journeyEventListener = JourneyEventListener { journeyId: Long, shared: String, flag: JourneyEnum ->
                Log.i("MYTEST", "CLICKED: $journeyId")
                if (flag == JourneyEnum.SHOW) {
                    findNavController()
                        .navigate(
                            ProfileFragmentDirections.actionProfileFragment2ToPlanMapFragment(
                                id = journeyId,
                                shared = "",
                                flag = "show"
                            )
                        )
                }
                if (flag == JourneyEnum.SHARE) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shared)
                        type = "text/html"
                    }
                    startActivity(Intent.createChooser(sendIntent,"Share"))
                }

            }
        )
        binding.journeysListRecyclerview.adapter = journeysAdapter

        profileViewModel.journeys.observe(viewLifecycleOwner) {
                if (it != null) {
                    Log.i("MYTEST","HALO : $it")
                    binding.numberOfJourneys.text = getString(R.string.total_destinations).plus(" ${it.size}")
                    var distance = 0.0
                    var durationHours = 0
                    var durationMinutes = 0
                    var days = 0
                    it.map { item ->
                        val tmp = item.totalDistance.split("km")[0].split(":")[1].trim()
                        distance += ((tmp.split(",")[0]).plus('.').plus(tmp.split(",")[1])).toDouble()
                        val hours = item.totalDuration.split(" h")[0].split(":")[1].trim().toInt()
                        durationHours += hours
                        val minutes = item.totalDuration.split("h")[1].split("m")[0].trim().toInt()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}