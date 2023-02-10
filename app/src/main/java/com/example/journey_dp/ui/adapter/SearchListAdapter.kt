package com.example.journey_dp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.database.model.DestinationItem
import com.example.journey_dp.databinding.DestinationItemBinding
import com.example.journey_dp.ui.adapter.events.DestinationEventListener

class SearchListAdapter(
    private val destinationEventListener: DestinationEventListener
) : ListAdapter<DestinationItem, SearchListAdapter.SearchItemViewHolder>(DiffCallback) {

    class SearchItemViewHolder(var binding: DestinationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(destinationEventListener: DestinationEventListener, destinationItem: DestinationItem) {
            binding.destination = destinationItem
            binding.destinationEventListener = destinationEventListener
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<DestinationItem>() {
        override fun areItemsTheSame(oldItem: DestinationItem, newItem: DestinationItem): Boolean {
            return oldItem.destinationName == newItem.destinationName
        }

        override fun areContentsTheSame(oldItem: DestinationItem, newItem: DestinationItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(
            DestinationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val destinationItem: DestinationItem = getItem(position)
        holder.bind(destinationEventListener, destinationItem)
    }

}