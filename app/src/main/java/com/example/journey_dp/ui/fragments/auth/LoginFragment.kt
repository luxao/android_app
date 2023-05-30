package com.example.journey_dp.ui.fragments.auth

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        if (auth.currentUser != null) {
            val action = LoginFragmentDirections.actionLoginFragmentToPlanJourneyFragment()
            findNavController().navigate(action)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        return binding.root
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                }
                else {
                    Log.w(TAG, "Google sign in failed")
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.w(TAG, "Google sign was canceled")
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Set click listener on Google Sign-In button
        binding.loginBtn.setOnClickListener {
            signIn()
        }
    }



    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    binding.loginWrapper.visibility = View.GONE
                    binding.loginAnimationWrapper.visibility = View.VISIBLE
                    binding.loginAnimation.playAnimation()
                    binding.loginAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            Log.i("MYTEST", "animation start")
                        }
                        override fun onAnimationEnd(animation: Animator) {
                            val action = LoginFragmentDirections.actionLoginFragmentToPlanJourneyFragment()
                            findNavController().navigate(action)
                        }
                        override fun onAnimationCancel(animation: Animator) {
                            Log.i("info", "animation cancel")
                        }
                        override fun onAnimationRepeat(animation: Animator) {
                            Log.i("info", "animation repeat")
                        }
                    })
                } else {
                    // Sign-In failure, display a message to the user
                    Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "MYTEST"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}