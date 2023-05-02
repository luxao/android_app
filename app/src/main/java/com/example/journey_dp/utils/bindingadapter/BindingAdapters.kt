package com.example.journey_dp.utils.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.data.firebase.UserJourney
import com.example.journey_dp.data.firebase.UserWithUID
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.data.room.model.RouteEntity
import com.example.journey_dp.ui.adapter.adapters.*


@BindingAdapter("journeys_list")
fun bindJourneysRecyclerView(recyclerView: RecyclerView, listData: MutableList<JourneyEntity>?) {
    val adapter = recyclerView.adapter as JourneysAdapter
    adapter.submitList(null)
    adapter.submitList(listData)
}

@BindingAdapter("users_list")
fun bindUsersToFindRecyclerView(recyclerView: RecyclerView, listData: MutableList<UserWithUID>?) {
    val adapter = recyclerView.adapter as UsersAdapter
    adapter.submitList(null)
    adapter.submitList(listData)
}

@BindingAdapter("followers_list")
fun bindFollowersRecyclerView(recyclerView: RecyclerView, listData: MutableList<UserWithUID>?) {
    val adapter = recyclerView.adapter as FollowersAdapter
    adapter.submitList(null)
    adapter.submitList(listData)
}



@BindingAdapter("notifications_list")
fun bindNotificationsRecyclerView(recyclerView: RecyclerView, listData: MutableList<UserWithUID>?) {
    val adapter = recyclerView.adapter as NotificationAdapter
    adapter.submitList(null)
    adapter.submitList(listData)
}


@BindingAdapter("user_journeys")
fun bindUserJourneysRecyclerView(recyclerView: RecyclerView, listData: MutableList<UserJourney>?) {
    val adapter = recyclerView.adapter as UserJourneysAdapter
    adapter.submitList(null)
    adapter.submitList(listData)
}