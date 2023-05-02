package com.example.journey_dp.ui.fragments.journey

import android.animation.Animator
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
import com.example.journey_dp.data.firebase.Routes
import com.example.journey_dp.data.firebase.UserJourney
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.data.room.model.RouteEntity

import com.example.journey_dp.databinding.FragmentProfileBinding
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter
import com.example.journey_dp.ui.adapter.events.JourneyEventListener
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.JourneyEnum
import com.example.journey_dp.utils.setAgainCharacter
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.math.round


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var journeysAdapter: JourneysAdapter
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref : DatabaseReference
    private lateinit var userId: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        if (auth.currentUser != null) {
            userId = auth.currentUser!!.uid
        }
        profileViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[ProfileViewModel::class.java]

        retrieveData()
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

        profileViewModel.journeys.observe(viewLifecycleOwner) { journey->
            if (journey.size <= 1) {
                ref.child("users_journeys").child(userId).get().addOnSuccessListener { snapshot ->
                    Log.i("MYTEST", "TESTUJEME ${snapshot.childrenCount}")
                    if ((snapshot.childrenCount.toInt() > journey.size).or(journey.isEmpty())) {
                        Log.i("MYTEST","SPUSTAM SE ${journey.size}")
                        if (profileViewModel.helperJourney.isNotEmpty()) {
                            Log.i("MYTEST","HELPERJOURNEY ${profileViewModel.helperJourney[0]}")
                            val ignoreName = profileViewModel.helperJourney[0].name
                            Log.i("MYTEST","Ignorujeeme meno $ignoreName")

                            for (item in snapshot.children) {
                                if (item.key != ignoreName) {
                                    val testJourney = item.getValue(UserJourney::class.java)

                                    val journeyToDB = JourneyEntity(
                                        user = testJourney?.user!!,
                                        name = testJourney.name,
                                        totalDistance = testJourney.totalDistance!!,
                                        totalDuration = testJourney.totalDuration!!,
                                        numberOfDestinations = testJourney.numberOfDestinations!!,
                                        sharedUrl = ""
                                    )
                                    val routesToDB = mutableListOf<RouteEntity>()
                                    Log.i("MYTEST","${journeyToDB.id} => $journeyToDB")
                                    for(route in testJourney.routes!!)  {
                                        val itemRoute = RouteEntity(
                                            journeyId = journeyToDB.id,
                                            origin = route.origin!!,
                                            destination = route.destination!!,
                                            travelMode = route.travelMode!!,
                                            note = route.note!!,
                                            originName = route.originName!!,
                                            destinationName = route.destinationName!!
                                        )
                                        routesToDB.add(itemRoute)
                                    }
                                    Log.i("MYTEST","$routesToDB")
                                    profileViewModel.insertJourneyWithDestinations(journeyToDB, routesToDB)
                                }
                            }
                        }
                        else {
                            Log.i("MYTEST","ELSE:")
                            for (item in snapshot.children) {
                                val testJourney = item.getValue(UserJourney::class.java)

                                val journeyToDB = JourneyEntity(
                                    user = testJourney?.user!!,
                                    name = testJourney.name,
                                    totalDistance = testJourney.totalDistance!!,
                                    totalDuration = testJourney.totalDuration!!,
                                    numberOfDestinations = testJourney.numberOfDestinations!!,
                                    sharedUrl = ""
                                )
                                val routesToDB = mutableListOf<RouteEntity>()
                                Log.i("MYTEST","${journeyToDB.id} => $journeyToDB")
                                for(route in testJourney.routes!!)  {
                                    val itemRoute = RouteEntity(
                                        journeyId = journeyToDB.id,
                                        origin = route.origin!!,
                                        destination = route.destination!!,
                                        travelMode = route.travelMode!!,
                                        note = route.note!!,
                                        originName = route.originName!!,
                                        destinationName = route.destinationName!!
                                    )
                                    routesToDB.add(itemRoute)
                                }
                                Log.i("MYTEST","$routesToDB")
                                profileViewModel.insertJourneyWithDestinations(journeyToDB, routesToDB)
                            }
                        }
                    }
                }.addOnFailureListener {
                    Log.e("MYTEST", "Error getting data",it)
                }

            }
        }



        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeJourney -> {
                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToPlanJourneyFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.planJourney -> {
                    binding.profileLayout.visibility = View.GONE
                    binding.animationProfileLayout.visibility = View.VISIBLE

                    binding.profileLoadingMapAnimation.playAnimation()
                    binding.profileLoadingMapAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            Log.i("MYTEST", "animation start")
                        }
                        override fun onAnimationEnd(animation: Animator) {
                            val action = ProfileFragmentDirections.actionProfileFragment2ToPlanMapFragment(
                                id = 0L,
                                shared = "",
                                flag = ""
                            )
                            view.findNavController().navigate(action)
                        }
                        override fun onAnimationCancel(animation: Animator) {
                            Log.i("info", "animation cancel")
                        }
                        override fun onAnimationRepeat(animation: Animator) {
                            Log.i("info", "animation repeat")
                        }
                    })
                    true
                }
                R.id.find_users -> {
                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToFindUsersFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.notifications -> {
                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToNotificationsFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.about -> {

                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToInfoFragment()
                    view.findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        journeysAdapter = JourneysAdapter(
            context = requireContext(),
            model = profileViewModel,
            userId = userId,
            ref = ref,
            journeyEventListener = JourneyEventListener { journeyId: Long, shared: String, flag: JourneyEnum ->
                Log.i("MYTEST", "CLICKED: $journeyId")
                if (flag == JourneyEnum.SHOW) {
                    setAgainCharacter()
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
                    setAgainCharacter()
                    var journeyName = ""
                    var distance = ""
                    var duration = ""
                    var destinations = 0
                    if (profileViewModel.helperJourney.isNotEmpty()) {
                        profileViewModel.helperJourney.map { journey ->
                            if (journeyId == journey.id) {
                                journeyName = journey.name
                                distance = journey.totalDistance
                                duration = journey.totalDuration
                                destinations = journey.numberOfDestinations
                                Log.i("MYTEST", "TEST TEST ${journey.name}")
                            }
                        }
                    }
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        val textToSend = "Journey was created by : ${auth.currentUser!!.displayName} \n" +
                                "Journey Name : $journeyName \n" +
                                "$distance \n" +
                                "$duration \n " +
                                "Number of destinations of journey : $destinations \n" +
                                "Click to link below to see journey your friend \n Link is : $shared"
                        putExtra(Intent.EXTRA_TEXT, textToSend)
                        type = "text/html"
                    }
                    startActivity(Intent.createChooser(sendIntent,"Share"))
                }

            }
        )
        binding.journeysListRecyclerview.adapter = journeysAdapter

        binding.followers.text = profileViewModel.getFollowers()
        binding.followed.text = profileViewModel.getFollowed()

        profileViewModel.journeys.observe(viewLifecycleOwner) {
                if (it != null) {
                    Log.i("MYTEST","HALO : $it")
                    binding.numberOfJourneys.text = getString(R.string.total_destinations).plus(" ${it.size}")
                    var distance = 0.0
                    var durationHours = 0
                    var durationMinutes = 0
                    var days = 0
                    it.map { item ->
                        profileViewModel.helperJourney.add(JourneyEntity(
                            id = item.id,
                            user = item.user,
                            name = item.name,
                            totalDistance = item.totalDistance,
                            totalDuration = item.totalDuration,
                            numberOfDestinations = item.numberOfDestinations,
                            sharedUrl = item.sharedUrl
                        ))
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


    private fun retrieveData() {
        ref.child("all_users").child(userId).get().addOnSuccessListener { snapshot ->
            for (snap in snapshot.children) {
                if (snap.key == "followers") {
                    profileViewModel.followers = snap.childrenCount
                    Log.i("MYTEST","FOLLOWERS: ${profileViewModel.followers}")
                }
                if (snap.key == "followed") {
                    profileViewModel.followed = snap.childrenCount
                    Log.i("MYTEST","FOLLOWED: ${profileViewModel.followed}")
                }
            }
        }.addOnFailureListener { error ->
            Log.e("MYTEST","ERROR : ${error.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}