package com.example.journey_dp.ui.adapter.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.data.room.model.RouteEntity
import com.example.journey_dp.databinding.JourneyDetailItemBinding
import com.example.journey_dp.utils.nextABC

class DetailsJourneyAdapter() : ListAdapter<RouteEntity, DetailsJourneyAdapter.DetailsItemViewHolder>(DiffCallback) {
    class DetailsItemViewHolder(var binding: JourneyDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(route: RouteEntity) {
            binding.route = route
            binding.originPointItem.text = nextABC().toString()
            binding.originItem.text = route.originName
            binding.destinationPointItem.text = nextABC().toString()
            binding.destinationItem.text = route.destinationName
            binding.noteToDestination.text = route.note
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<RouteEntity>() {
        override fun areItemsTheSame(oldItem: RouteEntity, newItem: RouteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RouteEntity, newItem: RouteEntity): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsJourneyAdapter.DetailsItemViewHolder {
        return DetailsJourneyAdapter.DetailsItemViewHolder(
            JourneyDetailItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DetailsJourneyAdapter.DetailsItemViewHolder, position: Int) {
        val route: RouteEntity = getItem(position)
        holder.bind(route)
    }





}