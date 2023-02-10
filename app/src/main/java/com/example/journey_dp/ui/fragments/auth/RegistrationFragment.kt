package com.example.journey_dp.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.journey_dp.databinding.FragmentRegistrationBinding


class RegistrationFragment : Fragment() {
    private var _binding : FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.loginBtn.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
            view.findNavController().navigate(action)
        }
    }
}