package com.example.journey_dp.ui.adapter.adapters


import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.journey_dp.R
import com.example.journey_dp.data.domain.Step
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.utils.calculateDistanceAndDuration

import com.example.journey_dp.utils.hideElements
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.maps.model.Marker

import com.google.android.gms.maps.model.Polyline

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.google.android.material.textfield.TextInputEditText


class InputAdapter(private var viewMap: View, private var stepsAdapter: StepsAdapter, private var recyclerView: RecyclerView,
                   private var imageAdapter: ImageAdapter, private var recyclerViewImage: RecyclerView,
                   private var steps: MutableList<List<Step>>, private var name: String, private var destination: String,
                   private val inputs: MutableList<LinearLayout>, private val markers: MutableList<Marker>,
                   private val polylines: MutableList<Polyline>, private var infoMarkers: MutableList<Marker>,
                   private val model: MapViewModel,private var standardBottomSheetBehavior: BottomSheetBehavior<View>,
                   private val placesClient: PlacesClient,
                   private val result: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<InputAdapter.ViewHolder>() {


    private var idPosition: Int = 0
    private var newOrigin = mutableListOf<String>()
    private var placeIds = mutableListOf<String>()


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
                placeIds.removeAt(holder.adapterPosition)

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
                placeIds.removeAt(holder.adapterPosition)
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

        val planWrapper = viewMap.findViewById<ConstraintLayout>(R.id.plan_wrapper)
        val userLocationInput = viewMap.findViewById<LinearLayout>(R.id.layout_for_add_station)
        val backButton = viewMap.findViewById<ImageView>(R.id.back_button)
        val placeWrapper = viewMap.findViewById<ConstraintLayout>(R.id.place_wrapper)
        val placeName = viewMap.findViewById<TextView>(R.id.place_name)
        val address = viewMap.findViewById<TextView>(R.id.address)
        val phone = viewMap.findViewById<TextView>(R.id.phone_number)
        val uriOfPage = viewMap.findViewById<TextView>(R.id.uri_of_page)
        val animationWrapper = viewMap.findViewById<ConstraintLayout>(R.id.animation_layout)
        val loadingAnimation = viewMap.findViewById<LottieAnimationView>(R.id.loading_animation)
        val bitmapList = mutableListOf<Bitmap>()

        holder.infoButton.setOnClickListener {
            userLocationInput.visibility = View.GONE
            planWrapper.visibility = View.GONE
            animationWrapper.visibility = View.VISIBLE

            standardBottomSheetBehavior.peekHeight = 700
            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            if (holder.inputText.text.toString().isNotBlank()) {
                placeName.text = holder.inputText.text.toString()

                val placeId = this.placeIds[holder.adapterPosition]

                val placeFields = listOf(Place.Field.ID,Place.Field.ADDRESS, Place.Field.WEBSITE_URI,Place.Field.PHONE_NUMBER,Place.Field.OPENING_HOURS,Place.Field.PHOTO_METADATAS)

                val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                placesClient.fetchPlace(request)
                    .addOnSuccessListener { response: FetchPlaceResponse ->
                        val place = response.place

                        if (place.address != null) {
                            address.text = place.address
                        }
                        else {
                            address.visibility = View.GONE
                        }
                        if (place.phoneNumber != null) {
                            phone.text = place.phoneNumber
                        }
                        else {
                            phone.visibility = View.GONE
                        }

                        if (place.websiteUri != null) {
                            uriOfPage.text = place.websiteUri!!.toString()
                        }
                        else {
                            uriOfPage.visibility = View.GONE
                        }

                        // Get the photo metadata.
                        val metadata = place.photoMetadatas
                        if (metadata == null || metadata.isEmpty()) {
                            Log.w("MYTEST", "No photo metadata.")
                            return@addOnSuccessListener
                        }
                        var counter = 0
                        loadingAnimation.playAnimation()
                        loadingAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                for (meta in metadata) {
                                    // Get the attribution text.
                                    val attributions = meta?.attributions

                                    // Create a FetchPhotoRequest.
                                    val photoRequest = FetchPhotoRequest.builder(meta)
                                        .setMaxWidth(500) // Optional.
                                        .setMaxHeight(300) // Optional.
                                        .build()
                                    placesClient.fetchPhoto(photoRequest)
                                        .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                                            val bitmap = fetchPhotoResponse.bitmap
                                            bitmapList.add(bitmap)
                                            counter += 1
                                        }.addOnFailureListener { exception: Exception ->
                                            if (exception is ApiException) {
                                                Log.e("MYTEST", "Place not found: " + exception.message)
                                                val statusCode = exception.statusCode
                                                TODO("Handle error with given status code.")
                                            }
                                        }

                                }
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                if (counter == metadata.size) {
                                    animationWrapper.visibility = View.GONE
                                    placeWrapper.visibility = View.VISIBLE
                                    recyclerViewImage.adapter = imageAdapter
                                    imageAdapter.submitList(bitmapList)
                                }
                                else {
                                    loadingAnimation.playAnimation()
                                }
                            }
                            override fun onAnimationCancel(animation: Animator) {
                                Log.i("info", "animation cancel")
                            }
                            override fun onAnimationRepeat(animation: Animator) {
                                Log.i("info", "animation repeat")
                            }
                        })


                    }

            }
        }


        backButton.setOnClickListener {
            placeWrapper.visibility = View.GONE
            userLocationInput.visibility = View.VISIBLE
            planWrapper.visibility = View.VISIBLE
        }

    }

    fun getNewOrigin(position: Int): String {
        if ((position == -1).or(position > newOrigin.size)) {
            throw Exception("List index out of range")
        }
        return this.newOrigin[position]

    }

    fun addPlaceId(placeId: String) {
        this.placeIds.add(placeId)
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
        val infoButton: ImageView = itemView.findViewById(R.id.info_button)
        val notesButton: ImageView = itemView.findViewById(R.id.note_button)
    }
}


