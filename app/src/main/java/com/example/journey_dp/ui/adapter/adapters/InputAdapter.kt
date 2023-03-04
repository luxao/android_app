package com.example.journey_dp.ui.adapter.adapters


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher

import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R
import com.example.journey_dp.data.domain.Step
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.utils.calculateDistanceAndDuration

import com.example.journey_dp.utils.hideElements

import com.google.android.gms.maps.model.Marker

import com.google.android.gms.maps.model.Polyline

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

import com.google.android.material.textfield.TextInputEditText
import kotlin.math.ceil


class InputAdapter(private var viewMap: View, private var stepsAdapter: StepsAdapter, private var recyclerView: RecyclerView,
                   private var steps: MutableList<List<Step>>, private var name: String, private var destination: String,
                   private val inputs: MutableList<LinearLayout>, private val markers: MutableList<Marker>,
                   private val polylines: MutableList<Polyline>, private var infoMarkers: MutableList<Marker>,
                   private val model: MapViewModel,
                   private val result: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<InputAdapter.ViewHolder>() {


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
                if (polylines.isNotEmpty()) {
                    var counter = 0
                    for (line in polylines) {
                        if (counter == holder.adapterPosition) {
                            line.remove()
                        }
                        counter+=1
                    }
                    val infoMark = infoMarkers.getOrNull(holder.adapterPosition)
                    infoMark?.remove()
                    infoMarkers.removeAt(holder.adapterPosition)
                    polylines.removeAt(holder.adapterPosition)
                }

                newOrigin.removeAt(holder.adapterPosition)

                calculateDistanceAndDuration(model.infoMarkers, viewMap)
            }
            idPosition = holder.adapterPosition
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(holder.itemView.context)
            result.launch(intent)
        }

        holder.inputText.isFocused.and(name.isNotBlank()).apply {
            newOrigin.add(holder.adapterPosition,destination)
            holder.inputText.setText(name)
        }

        holder.deleteButton.setOnClickListener{
            idPosition = holder.adapterPosition.minus(1)
            Log.i("MYTEST","POSITION AND ADAPTER POSITION : $idPosition and ${holder.adapterPosition}")
            if (steps.isNotEmpty().and(holder.inputText.text.toString().isNotBlank())) {
                steps.removeAt(holder.adapterPosition)
            }

            if (holder.inputText.text.toString().isNotBlank()) {
                Log.i("MYTEST", "IDPOSITION IS BEFORE DELETE: $idPosition and $newOrigin")
                val marker = markers.getOrNull(holder.adapterPosition.plus(1))
                marker?.remove()
                markers.removeAt(holder.adapterPosition.plus(1))
                if (polylines.isNotEmpty()) {
                    var counter = 0
                    for (line in polylines) {
                        if (counter == holder.adapterPosition) {
                            line.remove()
                        }
                        counter+=1
                    }
                    val infoMark = infoMarkers.getOrNull(holder.adapterPosition)
                    infoMark?.remove()
                    infoMarkers.removeAt(holder.adapterPosition)
                    polylines.removeAt(holder.adapterPosition)
                }

                newOrigin.removeAt(holder.adapterPosition)
                Log.i("MYTEST", "MARKERS: $markers")
                Log.i("MYTEST", "INFOMARKERS: $infoMarkers")
                Log.i("MYTEST", "POLYLINES: $polylines")
                Log.i("MYTEST", "IDPOSITION IS AFTER DELETE: $idPosition and $newOrigin")
            }

            if (idPosition == -1) {
                model.setLine("")
                model.setDirectionsToStart()
                Log.i("MYTEST", "CHECKLINE: ${model.checkLine}")
                hideElements(viewMap)
            }


            holder.inputText.setText("")
            inputs.removeAt(holder.adapterPosition)

            if (idPosition != -1) {
                if (steps.size > 0) {
                    recyclerView.adapter = stepsAdapter
                    stepsAdapter.submitList(steps[idPosition])
                }
            }

            calculateDistanceAndDuration(model.infoMarkers, viewMap)

            notifyItemRemoved(holder.adapterPosition)

        }

    }

    fun getNewOrigin(position: Int): String {
        if ((position == -1).or(position > newOrigin.size)) {
            throw Exception("List index out of range")
        }
        return this.newOrigin[position]

    }

    fun getID(): Int {
        return this.idPosition
    }

    fun setPosition(position: Int) {
        notifyItemChanged(position)
    }

    fun setName(name: String, destination: String) {
        this.name = name
        this.destination = destination
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_destination)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_input)
    }
}


