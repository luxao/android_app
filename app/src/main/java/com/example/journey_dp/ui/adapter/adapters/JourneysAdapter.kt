package com.example.journey_dp.ui.adapter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.data.domain.Journey
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.databinding.JourneyCardItemBinding

class JourneysAdapter() : ListAdapter<JourneyWithRoutes, JourneysAdapter.JourneyItemViewHolder>(DiffCallback) {

    class JourneyItemViewHolder(var binding: JourneyCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(journey: JourneyWithRoutes) {
            binding.journeyNameItem.text = journey.journey.name
            binding.totalDistanceItem.text =  journey.journey.totalDistance
            binding.totalDurationItem.text = journey.journey.totalDuration
            binding.totalDestinationsItem.text = journey.journey.numberOfDestinations.toString()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<JourneyWithRoutes>() {
        override fun areItemsTheSame(oldItem: JourneyWithRoutes, newItem: JourneyWithRoutes): Boolean {
            return oldItem.journey.id == newItem.journey.id
        }

        override fun areContentsTheSame(oldItem: JourneyWithRoutes, newItem: JourneyWithRoutes): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyItemViewHolder {
        return JourneyItemViewHolder(
            JourneyCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JourneyItemViewHolder, position: Int) {
        val journey: JourneyWithRoutes = getItem(position)
        holder.bind(journey)
    }

}

