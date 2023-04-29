package com.example.journey_dp.ui.adapter.events


class UserEventListener(val clickListener: (uid: String) -> Unit) {
    fun onClick(uid: String) = clickListener(uid)
}

