package com.example.journey_dp.ui.adapter.adapters


import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.journey_dp.data.firebase.UserJourney
import com.example.journey_dp.databinding.UserJourneyCardItemBinding
import com.example.journey_dp.ui.adapter.events.JourneyEventListener




class UserJourneysAdapter(
    private val journeyListener: JourneyEventListener
) : ListAdapter<UserJourney, UserJourneysAdapter.UserJourneyItemViewHolder>(DiffCallback) {

    class UserJourneyItemViewHolder(var binding: UserJourneyCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(journey: UserJourney, journeyListener: JourneyEventListener) {
            binding.userJourney = journey
            binding.journeyListener = journeyListener
            binding.journeyNameItem.text = journey.name
            binding.userTotalDurationItem.text = journey.totalDuration
            binding.userTotalDistanceItem.text = journey.totalDistance
            binding.userTotalDestinationsItem.text = journey.numberOfDestinations.toString()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<UserJourney>() {
        override fun areItemsTheSame(oldItem: UserJourney, newItem: UserJourney): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: UserJourney, newItem: UserJourney): Boolean {
            return oldItem.name == newItem.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserJourneyItemViewHolder {
        return UserJourneyItemViewHolder(
            UserJourneyCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserJourneyItemViewHolder, position: Int) {
        val journey: UserJourney = getItem(position)
        holder.bind(journey, journeyListener)
    }

}