package com.example.journey_dp.ui.adapter.adapters


import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri

import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.journey_dp.data.firebase.UserWithUID

import com.example.journey_dp.databinding.UserCardItemBinding

import com.example.journey_dp.ui.adapter.events.UserEventListener


class UsersAdapter(
    private val context: Context,
    private val userEventListener: UserEventListener
) : ListAdapter<UserWithUID, UsersAdapter.UserItemViewHolder>(DiffCallback) {

    class UserItemViewHolder(var binding: UserCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserWithUID, userEventListener: UserEventListener, context: Context) {
            binding.user = user
            binding.userName.text = user.userName
            binding.userEmail.text = user.userEmail
            binding.userProfilePicture.tag = user.userId
            Glide.with(context).load(user.userImage.toUri()).circleCrop().into(binding.userProfilePicture)
            binding.userListener = userEventListener
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
        holder.bind(user,userEventListener, context)
    }


}

