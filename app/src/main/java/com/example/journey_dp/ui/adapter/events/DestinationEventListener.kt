package com.example.journey_dp.ui.adapter.events

class DestinationEventListener(val clickListener: (destinationName: String?) -> Unit) {
    fun onClick(destinationName: String) = clickListener(destinationName)
}