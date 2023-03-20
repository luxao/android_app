package com.example.journey_dp.ui.fragments.journey


import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentPlanJourneyBinding
import com.example.journey_dp.ui.fragments.auth.LoginFragmentDirections


import com.example.journey_dp.utils.setLogOut
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8


class PlanJourneyFragment : Fragment() {

    private var _binding : FragmentPlanJourneyBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

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


        binding.startPlan.setOnClickListener {
            val action = PlanJourneyFragmentDirections.actionPlanJourneyFragmentToPlanMapFragment(
                id = 0L,
                shared = "",
                flag = ""
            )
            view.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}