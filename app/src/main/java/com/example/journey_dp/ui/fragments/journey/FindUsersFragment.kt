package com.example.journey_dp.ui.fragments.journey


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.journey_dp.R
import com.example.journey_dp.data.firebase.UserWithUID
import com.example.journey_dp.databinding.FragmentFindUsersBinding
import com.example.journey_dp.ui.adapter.adapters.FollowersAdapter
import com.example.journey_dp.ui.adapter.adapters.UsersAdapter
import com.example.journey_dp.ui.adapter.events.UserEventListener
import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.example.journey_dp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class FindUsersFragment : Fragment() {
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var followersAdapter: FollowersAdapter

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

        val loggedEmail = auth.currentUser!!.email
        val loggedImage = auth.currentUser!!.photoUrl.toString()
        val loggedName = auth.currentUser!!.displayName

        usersViewModel.loggedUser = UserWithUID(userId,loggedEmail!!, loggedImage, loggedName!!)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this@FindUsersFragment.viewLifecycleOwner
        binding.model = this@FindUsersFragment.usersViewModel


        val menu = binding.topAppBar.menu
        val profileItem = menu.findItem(R.id.profile)
        val imageItem = profileItem?.actionView as ImageView
        Glide.with(requireContext()).load(auth.currentUser?.photoUrl).centerInside().into(imageItem)
        imageItem.setOnClickListener {
            val action = FindUsersFragmentDirections.actionFindUsersFragmentToProfileFragment2()
            view.findNavController().navigate(action)
        }


        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val action = FindUsersFragmentDirections.actionFindUsersFragmentToPlanJourneyFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.profile -> {
                    val action = FindUsersFragmentDirections.actionFindUsersFragmentToProfileFragment2()
                    view.findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        followersAdapter = FollowersAdapter(
            context = requireContext(),
            userId = userId,
            usersViewModel = usersViewModel,
            ref = ref,
            userEventListener = UserEventListener { uid: String ->
                val action = FindUsersFragmentDirections.actionFindUsersFragmentToUserProfile(
                    userId = uid
                )
                view.findNavController().navigate(action)
            }
        )
        binding.followersRecyclerview.adapter = followersAdapter
        getUsers()


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
                ref = ref
            )
            usersRecyclerview.adapter = usersAdapter


            clearUsers.setOnClickListener {
                clearUsersList()
            }
        }
    }

    private fun getName() {
        binding.followersRecyclerview.visibility = View.GONE
        binding.usersRecyclerview.visibility = View.VISIBLE

        if (binding.searchFriendsInput.text.toString().isNotBlank()) {
            binding.clearWrapper.visibility = View.VISIBLE
            usersViewModel.userName = binding.searchFriendsInput.text.toString()
            usersViewModel.allUsers.map { user ->
                if (user.userName.contains(usersViewModel.userName)) {
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
        binding.followersRecyclerview.visibility = View.VISIBLE
        binding.usersRecyclerview.visibility = View.GONE
        binding.clearWrapper.visibility = View.GONE
        if (usersViewModel.searchedUsers.isNotEmpty()) {
            usersViewModel.searchedUsers.clear()
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
                    usersViewModel.allUsers.add(user)
                }
                else {
                    if (snap.child("requested").value != null) {
                        for (data in snap.child("requested").children){
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
                            usersViewModel.followingUsers.add(user)
                            followersAdapter.notifyItemInserted(usersViewModel.followingUsers.size)
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