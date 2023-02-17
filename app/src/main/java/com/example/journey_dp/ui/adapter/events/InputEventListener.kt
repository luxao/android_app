package com.example.journey_dp.ui.adapter.events

import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class InputEventListener(private val result: ActivityResultLauncher<Intent>, view: View){
    private val v = view
    fun onClick() {
        val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(v.context)
        result.launch(intent)
    }
}