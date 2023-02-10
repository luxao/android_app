package com.example.journey_dp.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.journey_dp.R
import com.example.journey_dp.databinding.FragmentLoginBinding
import com.example.journey_dp.databinding.FragmentRegistrationBinding


class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.registerBtn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            view.findNavController().navigate(action)
        }

        binding.loginBtn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToPlaneJourneyFragment()
            view.findNavController().navigate(action)
        }
    }


}