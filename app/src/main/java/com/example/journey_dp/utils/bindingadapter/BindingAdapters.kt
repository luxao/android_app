package com.example.journey_dp.utils.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.data.room.model.RouteEntity
import com.example.journey_dp.ui.adapter.adapters.DetailsJourneyAdapter
import com.example.journey_dp.ui.adapter.adapters.JourneysAdapter


@BindingAdapter("journeys_list")
fun bindJourneysRecyclerView(recyclerView: RecyclerView, listData: MutableList<JourneyEntity>?) {
    val adapter = recyclerView.adapter as JourneysAdapter
    adapter.submitList(null)
    adapter.submitList(listData)
}

//@BindingAdapter("details_list")
//fun bindDetailsRecyclerView(recyclerView: RecyclerView, listData: MutableList<RouteEntity>?) {
//    val adapter = recyclerView.adapter as DetailsJourneyAdapter
//    adapter.submitList(null)
//    adapter.submitList(listData)
//}