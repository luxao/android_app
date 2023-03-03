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
                   private var model: MapViewModel,private val result: ActivityResultLauncher<Intent>
                   ) : RecyclerView.Adapter<InputAdapter.ViewHolder>() {

    private var currentDistance: Double = 0.0
    private var currentDuration: Double = 0.0
    private var helperStringDistance: String = "Total Distance : "
    private var helperStringDuration: String = "Total Duration : "
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
        val duration = viewMap.findViewById<TextView>(R.id.totalDuration)
        val distance = viewMap.findViewById<TextView>(R.id.totalDistance)

        idPosition = holder.adapterPosition

        holder.inputText.focusable = View.NOT_FOCUSABLE

        holder.inputText.setOnClickListener {
            if (holder.inputText.text.toString().isNotBlank()) {
                model.totalDuration = model.totalDuration.minus(currentDuration)
                model.totalDistance = model.totalDistance.minus(currentDistance)
                distance.text = helperStringDistance.plus(model.totalDistance.toString()).plus(" \tkm")
                duration.text = helperStringDuration.plus(model.totalDuration.toString()).plus(" \th")

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
            Log.i("TEST", "STEPS ${steps.size}")
            Log.i("TEST", "STEPS $holder.adapterPosition")
            if (steps.isNotEmpty().and(holder.inputText.text.toString().isNotBlank())) {
                steps.removeAt(holder.adapterPosition)
            }


            if (idPosition == -1) {
                Log.i("TEST", "IDPOSITION IF MINUS 1 : $idPosition")
                model.setLine("")
                setDistance(0.0)
                setDuration(0.0)
                model.setCounterValue(0)
                model.setModelDistanceAndDuration(0.0, 0.0)
                distance.text = helperStringDistance.plus(model.totalDistance.toString()).plus(" \tkm")
                duration.text = helperStringDuration.plus(model.totalDuration.toString()).plus(" \th")
                hideElements(viewMap)
            }
            else {
                Log.i("TEST", "IDPOSITION IN ELSE : $idPosition")
                model.totalDuration = model.totalDuration.minus(currentDuration)
                model.totalDistance = model.totalDistance.minus(currentDistance)
                distance.text = helperStringDistance.plus(model.totalDistance.toString()).plus(" \tkm")
                duration.text = helperStringDuration.plus(model.totalDuration.toString()).plus(" \th")
            }


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

            }
            holder.inputText.setText("")
            inputs.removeAt(holder.adapterPosition)

            if (idPosition != -1) {
                recyclerView.adapter = stepsAdapter
                stepsAdapter.submitList(steps[idPosition])
            }

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

    fun setDuration(duration: Double) {
        this.currentDuration = duration
    }

    fun setDistance(distance: Double) {
        this.currentDistance = distance
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_destination)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_input)
    }
}

