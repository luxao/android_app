package com.example.journey_dp.ui.fragments.journey


import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.journey_dp.R
import com.example.journey_dp.data.firebase.User
import com.example.journey_dp.databinding.FragmentPlanJourneyBinding
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.utils.*
import com.example.journey_dp.utils.shared.LocationSharedPreferencesUtil
import com.example.journey_dp.utils.shared.SharedPreferencesUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*


class PlanJourneyFragment : Fragment() {

    private var _binding : FragmentPlanJourneyBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var model: MapViewModel
    private lateinit var database: FirebaseDatabase
    private lateinit var ref : DatabaseReference
    private lateinit var userId: String

    // Declaration of fusedLocationProvider
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getPermissions(requireContext())
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getPermissions(requireContext())
            }
            else -> {
                // No location access granted.
                Toast.makeText(context,"Permission was denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        if (auth.currentUser != null) {
            userId = auth.currentUser!!.uid
        }
        model = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[MapViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation(requireContext(),fusedLocationProviderClient, model, userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentPlanJourneyBinding.inflate(inflater, container, false)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userLoc = LocationSharedPreferencesUtil.getInstance().getUserUidLoc(context, userId)

        if (!checkPermissions(requireContext())) {
            requestPermissions()
        }

        val menu = binding.topAppBar.menu
        val profileItem = menu.findItem(R.id.user_profile)
        val imageItem = profileItem?.actionView as ImageView
        Glide.with(requireContext()).load(auth.currentUser?.photoUrl).centerInside().into(imageItem)
        imageItem.setOnClickListener {
            val action = PlanJourneyFragmentDirections.actionPlanJourneyFragmentToProfileFragment2()
            view.findNavController().navigate(action)
        }


        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user_profile -> {
                    val action = PlanJourneyFragmentDirections.actionPlanJourneyFragmentToProfileFragment2()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.action_logout -> {
                    auth.signOut()
                    googleSignInClient.signOut()
                    logOurDialog(requireActivity(), view, requireContext())
                    true
                }
                else -> false
            }
        }


        val newUser = User(
            userName = auth.currentUser!!.displayName!!,
            userEmail = auth.currentUser!!.email!!,
            userImage = auth.currentUser!!.photoUrl.toString()
        )

        val checkUser = SharedPreferencesUtil.getInstance().getUserUid(requireContext(), userId)
        if (checkUser!!.isBlank().or(checkUser.isEmpty())) {
            ref.child("all_users").get().addOnSuccessListener { snapshot ->
                if (snapshot.value == "") {
                    ref.child("all_users").child(userId).child("user_data").setValue(newUser)
                    ref.child("all_users").child(userId).child("requests").setValue("")
                    ref.child("all_users").child(userId).child("followed").setValue("")
                    ref.child("all_users").child(userId).child("followers").setValue("")
                }
                for (snap in snapshot.children) {
                    if (snap.key != userId) {
                        ref.child("all_users").child(userId).child("user_data").setValue(newUser)
                        ref.child("all_users").child(userId).child("requests").setValue("")
                        ref.child("all_users").child(userId).child("followed").setValue("")
                        ref.child("all_users").child(userId).child("followers").setValue("")
                        SharedPreferencesUtil.getInstance().putUserItem(requireContext(), userId)
                    }
                }
            }.addOnFailureListener {
                Log.e("MYTEST", "Error getting data", it)
            }
        }


        binding.startPlan.setOnClickListener {
            binding.introductionAnimationWrapper.visibility = View.GONE
            binding.loadingMapAnimationWrapper.visibility = View.VISIBLE
            binding.loadingMapAnimation.playAnimation()
            binding.loadingMapAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    if (userLoc!!.isBlank()) {
                        getLocation(requireContext(), fusedLocationProviderClient, model, userId)
                    }
                }
                override fun onAnimationEnd(animation: Animator) {
                    val action = PlanJourneyFragmentDirections.actionPlanJourneyFragmentToPlanMapFragment(
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

        }
    }



    private fun requestPermissions() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }


    @SuppressLint("MissingPermission")
    fun getPermissions(context: Context){
        if (checkPermissions(context)) {
            if (isLocationEnabled(context)) {
                Toast.makeText(context,"Thank you", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context,"Please turn on location", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            requestPermissions()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}