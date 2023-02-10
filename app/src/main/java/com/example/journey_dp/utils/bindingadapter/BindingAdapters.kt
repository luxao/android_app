package com.example.journey_dp.utils.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.database.model.DestinationItem
import com.example.journey_dp.ui.adapter.SearchListAdapter


@BindingAdapter("destination_data")
fun bindDestinationsRecyclerView(recyclerView: RecyclerView, listData: List<DestinationItem?>?) {
    val adapter = recyclerView.adapter as SearchListAdapter
    adapter.submitList(null)
    adapter.submitList(listData)

}