package com.example.journey_dp.ui.fragments.journey

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.journey_dp.R
import com.example.journey_dp.data.firebase.User
import com.example.journey_dp.data.firebase.UserWithUID
import com.example.journey_dp.databinding.FragmentFindUsersBinding
import com.example.journey_dp.databinding.FragmentProfileBinding
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter
import com.example.journey_dp.ui.adapter.adapters.UsersAdapter
import com.example.journey_dp.ui.adapter.events.JourneyEventListener
import com.example.journey_dp.ui.adapter.events.UserEventListener
import com.example.journey_dp.ui.viewmodel.ProfileViewModel
import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.example.journey_dp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class FindUsersFragment : Fragment() {
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var usersAdapter: UsersAdapter
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

        usersViewModel = ViewModelProvider(
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
        val loggedEmail = auth.currentUser!!.email
        val loggedImage = auth.currentUser!!.photoUrl.toString()
        val loggedName = auth.currentUser!!.displayName

        usersViewModel.loggedUser = UserWithUID(userId,loggedEmail!!, loggedImage, loggedName!!)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUsers()
        binding.lifecycleOwner = this@FindUsersFragment.viewLifecycleOwner
        binding.model = this@FindUsersFragment.usersViewModel


        binding.apply {
            searchFriendsInput.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    getName()
                }
            })

            usersAdapter = UsersAdapter(
                context = requireContext(),
                userId = userId,
                usersViewModel = usersViewModel,
                ref = ref,
                userEventListener = UserEventListener { uid: String ->
                    Log.i("MYTEST", "CLICKED: $uid")
                }
            )
            usersRecyclerview.adapter = usersAdapter

            clearUsers.setOnClickListener {
                clearUsersList()
            }
        }
    }

    private fun getName() {
        if (binding.searchFriendsInput.text.toString().isNotBlank()) {
            binding.clearWrapper.visibility = View.VISIBLE
            usersViewModel.userName = binding.searchFriendsInput.text.toString()
            usersViewModel.allUsers.map { user ->
                if (user.userName.contains(usersViewModel.userName)) {
                    Log.i("MYTEST", "FINDED USERS : ${user.userName}")
                    if (!usersViewModel.searchedUsers.contains(user)) {
                        usersViewModel.searchedUsers.add(user)
                        usersAdapter.notifyItemInserted(usersViewModel.searchedUsers.size)
                    }
                }
            }
        }
    }

    private fun clearUsersList() {
        binding.searchFriendsInput.text.clear()
        usersViewModel.userName = ""
        binding.clearWrapper.visibility = View.GONE
        if (usersViewModel.searchedUsers.isNotEmpty()) {
            usersViewModel.searchedUsers.clear()
            Log.i("MYTEST", "FINDED USERS IF EMPTY INPUT : ${usersViewModel.searchedUsers}")
            usersAdapter.notifyDataSetChanged()
        }
    }

    private fun getUsers() {
        ref.child("all_users").get().addOnSuccessListener { snapshot ->
            for (snap in snapshot.children) {
                if (snap.key != userId) {
                    val userUID = snap.key.toString()
                    val userEmail = snap.child("user_data").child("userEmail").value.toString()
                    val userImage = snap.child("user_data").child("userImage").value.toString()
                    val userName = snap.child("user_data").child("userName").value.toString()
                    val user = UserWithUID(userUID, userEmail, userImage, userName)
                    Log.i("MYTEST","$user")
                    usersViewModel.allUsers.add(user)
                }
                else {
                    if (snap.child("requested").value != null) {
                        for (data in snap.child("requested").children){
                            Log.i("MYTEST", "REQUESTED USERS: $data")
                            usersViewModel.requestedUsers.add(data.key.toString())
                        }
                    }

                    if (snap.child("followed").value != null) {
                        for (data in snap.child("followed").children) {
                            val userUID = data.key.toString()
                            val userEmail = data.child("userEmail").value.toString()
                            val userImage = data.child("userImage").value.toString()
                            val userName = data.child("userName").value.toString()
                            val user = UserWithUID(userUID, userEmail, userImage, userName)
                            Log.i("MYTEST","$user")
                            usersViewModel.followingUsers.add(user)
                        }
                    }
                }
            }
        }.addOnFailureListener { error ->
            Log.e("MYTEST", "ERROR ${error.message}")
        }.addOnCompleteListener {
            Log.i("MYTEST", "ALL : ${usersViewModel.allUsers}")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}