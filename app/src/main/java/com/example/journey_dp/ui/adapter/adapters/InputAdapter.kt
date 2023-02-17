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
import androidx.lifecycle.MutableLiveData

import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R

import com.example.journey_dp.ui.adapter.events.InputEventListener
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText



class InputAdapter(private var name: String, private val inputs: MutableList<LinearLayout>, private val result: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<InputAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.destination_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return inputs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.inputText.id = inputs.size

        holder.inputText.setOnClickListener(
            InputEventListener(result)
        )

        if (name.isNotBlank()) {
            holder.inputText.append(name)
        }

        holder.deleteButton.setOnClickListener{
            inputs.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

    }

    fun setName(name: String) {
        this.name = name
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_destination)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_input)
    }
}

