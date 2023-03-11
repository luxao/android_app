package com.example.journey_dp.ui.adapter.events

import com.example.journey_dp.utils.JourneyEnum


class JourneyEventListener(val clickListener: (id: Long, sharedUrl: String ,flag: JourneyEnum) -> Unit) {
    fun onClick(id: Long, sharedUrl: String,flag: JourneyEnum) = clickListener(id,sharedUrl,flag)
}



