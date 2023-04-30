package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.R
import com.example.journey_dp.data.firebase.UserWithUID
import com.example.journey_dp.databinding.FragmentFindUsersBinding
import com.example.journey_dp.databinding.FragmentNotificationsBinding
import com.example.journey_dp.ui.adapter.adapters.UsersAdapter
import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.example.journey_dp.utils.Injection
import com.example.journey_dp.utils.setFindUsersMenu
import com.example.journey_dp.utils.setNotificationsMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class NotificationsFragment : Fragment() {

    private lateinit var usersViewModel: UsersViewModel
    private var _binding : FragmentNotificationsBinding? = null
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

        usersViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext(),auth)
        )[UsersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        setNotificationsMenu(
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
        getNotifications()
    }

    private fun getNotifications() {
        ref.child("all_users").child(userId).get().addOnSuccessListener { snapshot ->
            for (snap in snapshot.children) {
                if (snap.key == "requests") {
                    Log.i("MYTEST", "${snap.value}")
                }
            }
        }.addOnFailureListener { error ->
            Log.e("MYTEST", "ERROR : ${error.message}")
        }

    }

}