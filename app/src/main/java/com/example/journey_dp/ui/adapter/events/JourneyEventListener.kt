package com.example.journey_dp.ui.adapter.events


import com.google.android.material.textfield.TextInputEditText

class JourneyEventListener(val clickListener: (input: TextInputEditText?) -> Unit) {
    fun onClick(input: TextInputEditText) = clickListener(input)
}



