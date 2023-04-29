package com.example.journey_dp.ui.adapter.adapters


import android.content.Context
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri

import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.journey_dp.data.firebase.User

import com.example.journey_dp.data.firebase.UserWithUID

import com.example.journey_dp.databinding.UserCardItemBinding

import com.example.journey_dp.ui.adapter.events.UserEventListener
import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.google.firebase.database.DatabaseReference


class UsersAdapter(
    private val context: Context,
    private val userId: String,
    private val usersViewModel: UsersViewModel,
    private val ref: DatabaseReference,
    private val userEventListener: UserEventListener
) : ListAdapter<UserWithUID, UsersAdapter.UserItemViewHolder>(DiffCallback) {

    class UserItemViewHolder(var binding: UserCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserWithUID, userEventListener: UserEventListener, context: Context, ref: DatabaseReference, userId: String,loggedUser: UserWithUID) {
            binding.user = user
            binding.userName.text = user.userName
            binding.userEmail.text = user.userEmail
            binding.userProfilePicture.tag = user.userId
            Glide.with(context).load(user.userImage.toUri()).circleCrop().into(binding.userProfilePicture)
            binding.userListener = userEventListener
            binding.followButton.setOnClickListener {
                Log.i("MYTEST", "FOLLOW CLICKED TAG ID : ${binding.followButton.tag}")
                binding.followButton.visibility = View.GONE
                binding.unfollowButton.visibility = View.VISIBLE
                binding.requestSend.visibility = View.VISIBLE

                ref.child("all_users").child(binding.followButton.tag.toString()).child("requests").child(userId).setValue(loggedUser)

            }
            binding.unfollowButton.setOnClickListener {
                Log.i("MYTEST", "UNFOLLOW CLICKED TAG ID : ${binding.unfollowButton.tag}")
                binding.followButton.visibility = View.VISIBLE
                binding.unfollowButton.visibility = View.GONE
                binding.requestSend.visibility = View.GONE

                ref.child("all_users").child(binding.followButton.tag.toString()).child("requests").child(userId).removeValue()

            }
        }

    }

    companion object DiffCallback: DiffUtil.ItemCallback<UserWithUID>() {
        override fun areItemsTheSame(oldItem: UserWithUID, newItem: UserWithUID): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserWithUID, newItem: UserWithUID): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        return UserItemViewHolder(
            UserCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        val user: UserWithUID = getItem(position)
        val loggedUser = UserWithUID(userId, usersViewModel.loggedUser.userEmail, usersViewModel.loggedUser.userImage, usersViewModel.loggedUser.userName)
        holder.bind(user,userEventListener, context, ref, userId, loggedUser)
    }

}


