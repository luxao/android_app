package com.example.journey_dp.ui.adapter.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.get
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R

import com.example.journey_dp.ui.adapter.events.InputEventListener
import com.example.journey_dp.ui.adapter.events.OnPlaceSelectedListener
import com.example.journey_dp.ui.fragments.maps.TestFragment
import com.example.journey_dp.ui.viewmodel.InputViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText



class InputAdapter(private var name: String, private val inputs: MutableList<LinearLayout>, private val result: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<InputAdapter.ViewHolder>() {


    private var idPosition: Int = 0


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

        idPosition = holder.adapterPosition

        holder.inputText.setOnClickListener {
            idPosition = holder.adapterPosition
            val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(holder.itemView.context)
            result.launch(intent)
        }

        holder.inputText.isFocused.and(name.isNotBlank()).apply {
            Log.i("TEST", "FOCUSED POSITION: ${holder.adapterPosition}, $position, ${holder.inputText.id},${holder.inputText.tag}")
            holder.inputText.setText(name)
        }

        holder.inputText.isActivated.and(name.isNotBlank()).apply {
            Log.i("TEST", "ACTIVATED POSITION: ${holder.adapterPosition}, $position, ${holder.inputText.id},${holder.inputText.tag}")
        }

        holder.deleteButton.setOnClickListener{
            idPosition = holder.adapterPosition - 1
            holder.inputText.setText("")
            inputs.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

    }

    fun getID(): Int {
        return this.idPosition
    }

    fun onPlaceSelected(place: Place, position: Int) {
        Log.i("TEST", "ADAPTER POSITION FROM onPlaceSelected: ${position}, ${place.name}")
        notifyItemChanged(position)
    }


    fun setName(name: String) {
        this.name = name
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputText: EditText = itemView.findViewById(R.id.input_destination)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_input)
    }
}

