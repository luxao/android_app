package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.journey_dp.R
import com.example.journey_dp.data.firebase.User
import com.example.journey_dp.data.firebase.UserWithUID
import com.example.journey_dp.databinding.FragmentFindUsersBinding
import com.example.journey_dp.databinding.FragmentProfileBinding
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.setFindUsersMenu
import com.example.journey_dp.utils.setMapMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class FindUsersFragment : Fragment() {
    private lateinit var model: UsersViewModel
    private var _binding : FragmentFindUsersBinding? = null
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

        model = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[UsersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindUsersBinding.inflate(inflater, container, false)
        setFindUsersMenu(
            context = requireContext(),
            activity = requireActivity() ,
            lifecycleOwner = viewLifecycleOwner,
            view = binding.root,
            auth = auth
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUsers()

        binding.apply {
            searchFriendsInput.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    getName()
                }
            })
        }
    }

    private fun getName() {
        if (binding.searchFriendsInput.text.toString().isNotBlank()) {
            model.userName = binding.searchFriendsInput.text.toString()
            Log.i("MYTEST", model.userName)
        }
    }

    private fun getUsers() {
        ref.child("all_users").get().addOnSuccessListener { snapshot ->
            for (snap in snapshot.children) {
                val userUID = snap.key.toString()
                val userEmail = snap.child("userEmail").value.toString()
                val userImage = snap.child("userImage").value.toString()
                val userName = snap.child("userName").value.toString()
                val user = UserWithUID(userUID, userEmail, userImage, userName)
                Log.i("MYTEST","$user")
                model.allUsers.add(user)
            }
        }.addOnFailureListener { error ->
            Log.e("MYTEST", "ERROR ${error.message}")
        }.addOnCompleteListener {
            Log.i("MYTEST", "ALL : ${model.allUsers}")
        }

    }



}