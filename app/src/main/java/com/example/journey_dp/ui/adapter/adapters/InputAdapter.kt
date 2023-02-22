package com.example.journey_dp.ui.adapter.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher

import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R
import com.example.journey_dp.ui.fragments.maps.TestMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText


class InputAdapter(private var name: String, private val inputs: MutableList<LinearLayout>, private val markers: MutableList<Marker>,private val result: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<InputAdapter.ViewHolder>() {


    private var idPosition: Int = 0
    private var newOrigin = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.destination_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return inputs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.inputText.id = position
        holder.inputText.tag = "input_$position"

        val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        idPosition = holder.adapterPosition

        holder.inputText.focusable = View.NOT_FOCUSABLE

        holder.inputText.setOnClickListener {
            if (holder.inputText.text.toString().isNotBlank()) {
                val marker = markers.getOrNull(holder.adapterPosition.plus(1))
                marker?.remove()
                markers.removeAt(holder.adapterPosition.plus(1))
                newOrigin.removeAt(holder.adapterPosition)
            }
            idPosition = holder.adapterPosition
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(holder.itemView.context)
            result.launch(intent)
        }

        holder.inputText.isFocused.and(name.isNotBlank()).apply {
            newOrigin.add(holder.adapterPosition,name)
            holder.inputText.setText(name)
        }

        holder.deleteButton.setOnClickListener{
            idPosition = holder.adapterPosition - 1
            if (holder.inputText.text.toString().isNotBlank()) {
                val marker = markers.getOrNull(holder.adapterPosition.plus(1))
                marker?.remove()
                markers.removeAt(holder.adapterPosition.plus(1))
                newOrigin.removeAt(holder.adapterPosition)
            }
            holder.inputText.setText("")
            inputs.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

        }

    }

    fun getNewOrigin(position: Int): String {
        return this.newOrigin[position]
    }

    fun getID(): Int {
        return this.idPosition
    }

    fun setPosition(position: Int) {
        notifyItemChanged(position)
    }

    fun setName(name: String) {
        this.name = name
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_destination)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_input)
        val imageOfDirection: ImageView = itemView.findViewById(R.id.mode_of_travel)
    }
}

