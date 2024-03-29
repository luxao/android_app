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
import com.example.journey_dp.databinding.NotificationCardItemBinding
import com.example.journey_dp.ui.viewmodel.UsersViewModel
import com.google.firebase.database.DatabaseReference

class NotificationAdapter(
    private val context: Context,
    private val userId: String,
    private val usersViewModel: UsersViewModel,
    private val ref: DatabaseReference
) : ListAdapter<UserWithUID, NotificationAdapter.NotificationItemViewHolder>(DiffCallback) {

    class NotificationItemViewHolder(var binding: NotificationCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserWithUID,  context: Context, ref: DatabaseReference, userId: String, loggedUser: UserWithUID) {
            binding.user = user
            binding.notificationUserPicture.tag = user.userId
            Glide.with(context).load(user.userImage.toUri()).circleCrop().into(binding.notificationUserPicture)
            binding.notificationUserName.text = user.userName.plus(" wants to follow you")

            binding.acceptBtn.setOnClickListener {
                binding.layoutForAccept.visibility = View.GONE

                ref.child("all_users").child(userId).child("followers").child(user.userId).setValue(user)
                ref.child("all_users").child(userId).child("requests").child(user.userId).removeValue()
                ref.child("all_users").child(user.userId).child("followed").child(userId).setValue(loggedUser)
                ref.child("all_users").child(user.userId).child("requested").child(userId).removeValue()
                binding.notificationUserName.text = user.userName.plus(" started following you")
            }

            binding.cancelRequest.setOnClickListener {
                binding.layoutForAccept.visibility = View.GONE

                ref.child("all_users").child(userId).child("requests").child(user.userId).removeValue()
                ref.child("all_users").child(user.userId).child("requested").child(userId).removeValue()
                val info = "Request of ".plus(user.userName).plus(" was cancelled")
                binding.notificationUserName.text = info

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemViewHolder {
        return NotificationItemViewHolder(
            NotificationCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationItemViewHolder, position: Int) {
        val user: UserWithUID = getItem(position)
        val loggedUser = UserWithUID(userId, usersViewModel.loggedUser.userEmail, usersViewModel.loggedUser.userImage, usersViewModel.loggedUser.userName)
        holder.bind(user, context, ref, userId, loggedUser)
    }

}