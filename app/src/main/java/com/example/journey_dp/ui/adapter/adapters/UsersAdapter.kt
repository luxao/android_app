package com.example.journey_dp.ui.adapter.adapters


import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri

import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.journey_dp.data.firebase.UserWithUID

import com.example.journey_dp.databinding.UserCardItemBinding

import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.google.firebase.database.DatabaseReference


class UsersAdapter(
    private val context: Context,
    private val userId: String,
    private val usersViewModel: UsersViewModel,
    private val ref: DatabaseReference
) : ListAdapter<UserWithUID, UsersAdapter.UserItemViewHolder>(DiffCallback) {

    class UserItemViewHolder(var binding: UserCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserWithUID, context: Context, ref: DatabaseReference, userId: String,loggedUser: UserWithUID,
                 requested: MutableList<String>, following: MutableList<UserWithUID>) {
            binding.user = user
            binding.userName.text = user.userName
            binding.userEmail.text = user.userEmail
            binding.userProfilePicture.tag = user.userId
            Glide.with(context).load(user.userImage.toUri()).circleCrop().into(binding.userProfilePicture)
            if (requested.isNotEmpty()) {
                requested.map {
                    if (it == user.userId) {
                        binding.followButton.visibility = View.GONE
                        binding.unfollowButton.visibility = View.VISIBLE
                        binding.requestSend.visibility = View.VISIBLE
                    }
                }
            }

            if (following.isNotEmpty()) {
                following.map {
                    if (it.userId == user.userId) {
                        binding.followButton.visibility = View.GONE
                        binding.following.visibility = View.VISIBLE
                        binding.unfollowButton.visibility = View.VISIBLE
                        binding.requestSend.visibility = View.GONE
                    }
                }
            }

             binding.followButton.setOnClickListener {
                binding.followButton.visibility = View.GONE
                binding.unfollowButton.visibility = View.VISIBLE
                binding.requestSend.visibility = View.VISIBLE

                ref.child("all_users").child(binding.followButton.tag.toString()).child("requests").child(userId).setValue(loggedUser)
                ref.child("all_users").child(userId).child("requested").child(binding.followButton.tag.toString()).setValue(binding.followButton.tag.toString())
            }
            binding.unfollowButton.setOnClickListener {
                binding.followButton.visibility = View.VISIBLE
                binding.unfollowButton.visibility = View.GONE
                binding.requestSend.visibility = View.GONE

                ref.child("all_users").child(binding.followButton.tag.toString()).child("requests").child(userId).removeValue()
                ref.child("all_users").child(userId).child("requested").child(binding.followButton.tag.toString()).removeValue()

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
        val requested = usersViewModel.requestedUsers
        val following = usersViewModel.followingUsers
        holder.bind(user, context, ref, userId, loggedUser, requested, following)
    }

}


