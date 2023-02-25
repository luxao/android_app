package com.example.journey_dp.utils.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R

import com.example.journey_dp.ui.adapter.adapters.InputAdapter

@BindingAdapter("directionsType")
fun chooseDirectionsTypIcon(imageView: ImageView, iconType: String?) {
    imageView.setImageResource(
        when(iconType){
            "driving" -> R.drawable.ic_baseline_directions_car_24
            "bus" -> R.drawable.ic_baseline_directions_bus_24
            "train" -> R.drawable.ic_baseline_directions_transit_filled_24
            "walking" -> R.drawable.ic_baseline_directions_walk_24
            "bicycling" -> R.drawable.ic_baseline_directions_bike_24
            else -> R.drawable.ic_baseline_mode_of_travel_24
        }
    )
}


