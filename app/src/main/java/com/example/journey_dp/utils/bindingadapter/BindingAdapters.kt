package com.example.journey_dp.utils.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter


@BindingAdapter("journeys_list")
fun bindJourneysRecyclerView(recyclerView: RecyclerView, listData: MutableList<JourneyWithRoutes>?) {
    val adapter = recyclerView.adapter as JourneysAdapter
    adapter.submitList(null)
    adapter.submitList(listData)
}