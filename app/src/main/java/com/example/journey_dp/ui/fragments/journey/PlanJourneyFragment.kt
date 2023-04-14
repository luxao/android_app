package com.example.journey_dp.ui.fragments.journey


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentPlanJourneyBinding
import com.example.journey_dp.ui.fragments.auth.LoginFragmentDirections
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.utils.*


import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8
import java.util.*


class PlanJourneyFragment : Fragment() {

    private var _binding : FragmentPlanJourneyBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var model: MapViewModel

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

        model = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[MapViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation(requireContext(),fusedLocationProviderClient, model)
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
        setLogOut(
            context = requireContext(),
            activity = requireActivity() ,
            lifecycleOwner = viewLifecycleOwner,
            view = binding.root,
            googleSignInClient = googleSignInClient,
            auth = auth
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!checkPermissions(requireContext())) {
            requestPermissions()
        }

        binding.startPlan.setOnClickListener {
            val action = PlanJourneyFragmentDirections.actionPlanJourneyFragmentToPlanMapFragment(
                id = 0L,
                shared = "",
                flag = ""
            )
            view.findNavController().navigate(action)
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