package com.example.journey_dp.ui.adapter.events


import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

class DestinationEventListener(val clickListener: (input: TextInputEditText?) -> Unit) {
    fun onClick(input: TextInputEditText) = clickListener(input)
}



