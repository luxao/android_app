package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.journey_dp.R
import com.example.journey_dp.data.firebase.UserJourney

import com.example.journey_dp.databinding.FragmentUserProfileBinding
import com.example.journey_dp.ui.adapter.adapters.UserJourneysAdapter
import com.example.journey_dp.ui.adapter.events.JourneyEventListener

import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.JourneyEnum
import com.example.journey_dp.utils.setAgainCharacter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase



class UserProfileFragment : Fragment() {
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var userJourneysAdapter: UserJourneysAdapter
    private var _binding : FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref : DatabaseReference
    private lateinit var userId: String

    private val userProfileArgs: UserProfileFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        usersViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[UsersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this@UserProfileFragment.viewLifecycleOwner
        binding.model = this@UserProfileFragment.usersViewModel


        if (userProfileArgs.userId.isNotBlank().or(userProfileArgs.userId.isNotEmpty())) {
            userId = userProfileArgs.userId
            userJourneysAdapter = UserJourneysAdapter(
                journeyListener = JourneyEventListener { journeyId: Long, shared: String, flag: JourneyEnum ->
                    if (flag == JourneyEnum.SHARE) {
                        setAgainCharacter()
                        findNavController()
                            .navigate(
                                UserProfileFragmentDirections.actionUserProfileToPlanMapFragment(
                                    id = journeyId,
                                    shared = shared.split("=")[1],
                                    flag = "share"
                                )
                            )
                    }
                }
            )
            binding.userJourneysRecyclerview.adapter = userJourneysAdapter
            binding.backToFollowers.setOnClickListener {
                val action = UserProfileFragmentDirections.actionUserProfileToFindUsersFragment()
                view.findNavController().navigate(action)
            }
            getUserProfileInfo(userProfileArgs.userId)
            getUserJourneys(userProfileArgs.userId)
        }
    }


    private fun getUserProfileInfo(uid: String) {
        ref.child("all_users").get().addOnSuccessListener { snapshot ->
            for (snap in snapshot.children) {
                if (snap.key == uid) {
                    userId = snap.key.toString()
                    val userEmail = snap.child("user_data").child("userEmail").value.toString()
                    val userImage = snap.child("user_data").child("userImage").value.toString()
                    val userName = snap.child("user_data").child("userName").value.toString()
                    Glide.with(requireContext()).load(userImage.toUri()).circleCrop().into(binding.userProfilePicture)
                    binding.userProfileName.text = userName
                    binding.userProfileEmail.text = userEmail
                    binding.userFollowers.text = " ".plus(getString(R.string.followers)).plus(" ").plus(snap.child("followers").childrenCount)
                    binding.userFollowed.text = " ".plus(getString(R.string.followed)).plus(" ").plus(snap.child("followed").childrenCount)

                }
            }
        }
    }


    private fun getUserJourneys(uid: String) {
        ref.child("users_journeys").child(uid).get().addOnSuccessListener { snapshot ->
            for (snap in snapshot.children) {
                val testJourney = snap.getValue(UserJourney::class.java)

                usersViewModel.userJourneys.add(testJourney!!)
                userJourneysAdapter.notifyItemInserted(usersViewModel.userJourneys.size)
            }
        }.addOnFailureListener {  error ->
            Log.e("MYTEST", "ERROR : ${error.message}")
        }
    }



}