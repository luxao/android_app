package com.example.journey_dp.ui.adapter.events


class JourneyEventListener(val clickListener: (id: Long) -> Unit) {
    fun onClick(id: Long) = clickListener(id)
}



