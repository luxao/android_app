package com.example.journey_dp.ui.adapter.events

import com.example.journey_dp.utils.JourneyEnum


class JourneyEventListener(val clickListener: (id: Long, flag: JourneyEnum) -> Unit) {
    fun onClick(id: Long, flag: JourneyEnum) = clickListener(id,flag)
}



