package com.example.journey_dp.ui.fragments.journey

import android.os.Bundle
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
import com.example.journey_dp.databinding.FragmentNotificationsBinding
import com.example.journey_dp.ui.adapter.adapters.NotificationAdapter
import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.example.journey_dp.utils.Injection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class NotificationsFragment : Fragment() {

    private lateinit var usersViewModel: UsersViewModel
    private lateinit var notificationAdapter: NotificationAdapter
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

        val loggedEmail = auth.currentUser!!.email
        val loggedImage = auth.currentUser!!.photoUrl.toString()
        val loggedName = auth.currentUser!!.displayName

        usersViewModel.loggedUser = UserWithUID(userId,loggedEmail!!, loggedImage, loggedName!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this@NotificationsFragment.viewLifecycleOwner
        binding.model = this@NotificationsFragment.usersViewModel



        val menu = binding.topAppBar.menu
        val profileItem = menu.findItem(R.id.profile)
        val imageItem = profileItem?.actionView as ImageView
        Glide.with(requireContext()).load(auth.currentUser?.photoUrl).centerInside().into(imageItem)
        imageItem.setOnClickListener {
            val action = NotificationsFragmentDirections.actionNotificationsFragmentToProfileFragment2()
            view.findNavController().navigate(action)
        }


        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val action = NotificationsFragmentDirections.actionNotificationsFragmentToPlanJourneyFragment()
                    view.findNavController().navigate(action)
                    true
                }
                R.id.profile -> {

                    val action = NotificationsFragmentDirections.actionNotificationsFragmentToProfileFragment2()
                    view.findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        notificationAdapter = NotificationAdapter(
            context = requireContext(),
            userId = userId,
            usersViewModel = usersViewModel,
            ref = ref,
        )

        binding.notificationsRecyclerview.adapter = notificationAdapter

        getNotifications()
    }

    private fun getNotifications() {
        ref.child("all_users").child(userId).get().addOnSuccessListener { snapshot ->
            for (snap in snapshot.children) {
                if (snap.key == "requests") {
                    for (data in snap.children) {
                        val userUID = data.key.toString()
                        val userEmail = data.child("userEmail").value.toString()
                        val userImage = data.child("userImage").value.toString()
                        val userName = data.child("userName").value.toString()
                        val user = UserWithUID(userUID, userEmail, userImage, userName)
                        usersViewModel.requestsList.add(user)
                        notificationAdapter.notifyItemInserted(usersViewModel.requestsList.size)
                    }

                }
            }
        }.addOnFailureListener { error ->
            Log.e("MYTEST", "ERROR : ${error.message}")
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}