package com.example.journey_dp.ui.adapter.adapters


import android.animation.Animator
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop

import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.journey_dp.R
import com.example.journey_dp.ui.viewmodel.MapViewModel
import com.example.journey_dp.utils.calculateDistanceAndDuration
import com.example.journey_dp.utils.callIntent

import com.example.journey_dp.utils.hideElements
import com.example.journey_dp.utils.showWebPageIntent
import com.google.android.gms.common.api.ApiException


import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class InputAdapter(private var viewMap: View, private var cont: Context, private var stepsAdapter: StepsAdapter, private var recyclerView: RecyclerView,
                   private var imageAdapter: ImageAdapter, private var recyclerViewImage: RecyclerView,
                   private var name: String, private var destination: String,
                   private val model: MapViewModel, private var standardBottomSheetBehavior: BottomSheetBehavior<View>,
                   private val placesClient: PlacesClient,
                   private val result: ActivityResultLauncher<Intent>) : RecyclerView.Adapter<InputAdapter.ViewHolder>() {


    private var idPosition: Int = 0



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.destination_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return model.inputs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.inputText.id = position
            holder.inputText.tag = "input_$position"

            val listFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            val planWrapper = viewMap.findViewById<ConstraintLayout>(R.id.plan_wrapper)
            val userLocationInput = viewMap.findViewById<LinearLayout>(R.id.layout_for_add_station)
            val backButton = viewMap.findViewById<ImageView>(R.id.back_button)
            val placeWrapper = viewMap.findViewById<ConstraintLayout>(R.id.place_wrapper)
            val placeName = viewMap.findViewById<TextView>(R.id.place_name)
            val wikiInfo = viewMap.findViewById<TextView>(R.id.wiki_info)
            val address = viewMap.findViewById<TextView>(R.id.address)
            val phone = viewMap.findViewById<TextView>(R.id.phone_number)
            val uriOfPage = viewMap.findViewById<TextView>(R.id.uri_of_page)
            val animationWrapper = viewMap.findViewById<ConstraintLayout>(R.id.animation_layout)
            val notesWrapper = viewMap.findViewById<ConstraintLayout>(R.id.notes_wrapper)
            val loadingAnimation = viewMap.findViewById<LottieAnimationView>(R.id.loading_animation)
            val backButtonNote = viewMap.findViewById<ImageView>(R.id.back_button_from_notes)
            var bitmaps: MutableList<Bitmap>
            var placeId = ""


            idPosition = holder.adapterPosition
            Log.i("MYTEST", "HOLDER ADAPTER POSITION ON START ${holder.adapterPosition} and IDPOS : $idPosition")

            model.bitmapList.add(holder.adapterPosition, mutableListOf())
            model.addressList.add(holder.adapterPosition, "")
            model.phoneList.add(holder.adapterPosition, "")
            model.websiteList.add(holder.adapterPosition, "")
            model.wikiInfoList.add(holder.adapterPosition, "")

            holder.inputText.focusable = View.NOT_FOCUSABLE

            holder.inputText.setOnClickListener {
                idPosition = holder.adapterPosition
                if (holder.inputText.text.toString().isNotBlank()) {
                    model.changeBetweenWaypoints = true
                }
                idPosition = holder.adapterPosition
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,listFields).build(holder.itemView.context)
                result.launch(intent)
            }


            holder.inputText.isFocused.and(name.isNotBlank()).apply {
                model.newOrigin.add(holder.adapterPosition,destination)
                model.destinationsName.add(holder.adapterPosition.plus(1), name)
                holder.inputText.setText(name)
            }


            holder.deleteButton.setOnClickListener{
                idPosition = holder.adapterPosition.minus(1)
                Log.i("MYTEST", "HOLDER ADAPTER POSITION AFTER DELETE ${holder.adapterPosition} and IDPOS : $idPosition")

                Log.i("MYTEST","POSITION AND ADAPTER POSITION : $idPosition and ${holder.adapterPosition}")
                if (model.stepsList.isNotEmpty().and(holder.inputText.text.toString().isNotBlank())) {
                    model.stepsList.removeAt(holder.adapterPosition)
                }

                if (holder.inputText.text.toString().isNotBlank()) {
                    Log.i("MYTEST", "IDPOSITION IS BEFORE DELETE: $idPosition and ${model.newOrigin}")
                    Log.i("MYTEST", "MARKERS BEFORE DELETE ${model.markers}")
                    val marker = model.markers.getOrNull(holder.adapterPosition.plus(1))
                    marker?.remove()
                    model.markers.removeAt(holder.adapterPosition.plus(1))
                    if (model.polylines.isNotEmpty()) {
                        var counter = 0
                        for (line in model.polylines) {
                            if (counter == holder.adapterPosition) {
                                line.remove()
                            }
                            counter+=1
                        }
                        val infoMark = model.infoMarkers.getOrNull(holder.adapterPosition)
                        infoMark?.remove()
                        model.infoMarkers.removeAt(holder.adapterPosition)
                        model.polylines.removeAt(holder.adapterPosition)
                    }

                    if (model.newOrigin.isNotEmpty()) {
                        model.newOrigin.removeAt(holder.adapterPosition)
                        model.destinationsName.removeAt(holder.adapterPosition.plus(1))
                    }

                    model.placeIds.removeAt(holder.adapterPosition)

                    if (model.notes[holder.adapterPosition].isNotEmpty()) {
                        model.notes.removeAt(holder.adapterPosition)
                    }
                    if (model.travelMode.isNotEmpty()) {
                        model.travelMode.removeAt(holder.adapterPosition)
                    }

                    model.bitmapList.removeAt(holder.adapterPosition)
                    model.addressList.removeAt(holder.adapterPosition)
                    model.phoneList.removeAt(holder.adapterPosition)
                    model.websiteList.removeAt(holder.adapterPosition)
                    model.wikiInfoList.removeAt(holder.adapterPosition)

                    Log.i("MYTEST", "MARKERS: ${model.markers}")
                    Log.i("MYTEST", "INFOMARKERS: ${model.infoMarkers}")
                    Log.i("MYTEST", "POLYLINES: ${model.polylines}")
                    Log.i("MYTEST", "IDPOSITION IS AFTER DELETE: $idPosition and ${model.newOrigin}")
                }

                if (idPosition == -1) {
                    model.setLine("")
                    model.setDirectionsToStart()
                    hideElements(viewMap)
                }


                holder.inputText.setText("")
                model.inputs.removeAt(holder.adapterPosition)

                if (idPosition != -1) {
                    if (model.stepsList.size > 0) {
                        recyclerView.adapter = stepsAdapter
                        stepsAdapter.submitList(model.stepsList[idPosition])
                    }
                }

                calculateDistanceAndDuration(model.infoMarkers, viewMap)

                notifyItemRemoved(holder.adapterPosition)

            }



            holder.infoButton.setOnClickListener {
                userLocationInput.visibility = View.GONE
                planWrapper.visibility = View.GONE

                bitmaps = mutableListOf()
                standardBottomSheetBehavior.peekHeight = 700
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                if (holder.inputText.text.toString().isNotBlank()) {
                    placeName.text = holder.inputText.text.toString()

                    placeId = model.placeIds[holder.adapterPosition]
                    Log.i("MYTEST", "Bitmap list ${model.bitmapList} ")

                    if (model.bitmapList[holder.adapterPosition].isNotEmpty()) {
                        placeWrapper.visibility = View.VISIBLE
                        recyclerViewImage.adapter = imageAdapter
                        imageAdapter.submitList(model.bitmapList[holder.adapterPosition])
                        if (model.addressList[holder.adapterPosition].isNotEmpty()) {
                            address.text = model.addressList[holder.adapterPosition]
                        }
                        else {
                            address.visibility = View.GONE
                        }

                        if (model.phoneList[holder.adapterPosition].isNotEmpty()) {
                            phone.text = model.phoneList[holder.adapterPosition]
                            phone.setOnClickListener {
                                callIntent(phone.text.toString(), viewMap.context)
                            }
                        }
                        else {
                            phone.visibility = View.GONE
                        }

                        if (model.websiteList[holder.adapterPosition].isNotEmpty()) {
                            uriOfPage.text = model.websiteList[holder.adapterPosition]
                            uriOfPage.setOnClickListener {
                                showWebPageIntent(uriOfPage.text.toString(), viewMap.context)
                            }
                        }
                        else {
                            uriOfPage.visibility = View.GONE
                        }

                        if (model.wikiInfoList[holder.adapterPosition].isNotEmpty()) {
                            wikiInfo.text = model.wikiInfoList[holder.adapterPosition]
                        }
                        else {
                            wikiInfo.visibility = View.GONE
                        }
                    }

                    else {
                        animationWrapper.visibility = View.VISIBLE
                        val placeFields = listOf(Place.Field.ID,Place.Field.ADDRESS, Place.Field.WEBSITE_URI,Place.Field.PHONE_NUMBER,Place.Field.OPENING_HOURS,Place.Field.PHOTO_METADATAS)
                        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                        model.getWikiInfo(
                            placeName.text.toString(),
                            successCallback = { cityInfo ->
                                wikiInfo.text = cityInfo
                                model.wikiInfoList.add(holder.adapterPosition, cityInfo)
                            },
                            errorCallback = { error ->
                                Log.e("MYTEST", "ERROR : $error")
                                wikiInfo.visibility = View.GONE
                            }
                        )


                        placesClient.fetchPlace(request)
                            .addOnSuccessListener { response: FetchPlaceResponse ->
                                val place = response.place

                                if (place.address != null) {
                                    address.text = place.address
                                    model.addressList.add(holder.adapterPosition, place.address!!)
                                }
                                else {
                                    address.visibility = View.GONE
                                }
                                if (place.phoneNumber != null) {
                                    phone.text = place.phoneNumber
                                    phone.setOnClickListener {
                                        callIntent(phone.text.toString(), viewMap.context)
                                    }
                                    model.phoneList.add(holder.adapterPosition, place.phoneNumber!!)
                                }
                                else {
                                    phone.visibility = View.GONE
                                }

                                if (place.websiteUri != null) {
                                    uriOfPage.text = place.websiteUri!!.toString()
                                    uriOfPage.setOnClickListener {
                                        showWebPageIntent(uriOfPage.text.toString(), viewMap.context)
                                    }
                                    model.websiteList.add(holder.adapterPosition, place.websiteUri!!.toString())
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

                                loadingAnimation.playAnimation()


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
                                            bitmaps.add(bitmap)
                                        }.addOnFailureListener { exception: Exception ->
                                            if (exception is ApiException) {
                                                Log.e("MYTEST", "Place not found: " + exception.message)
                                                val statusCode = exception.statusCode
                                                TODO("Handle error with given status code.")
                                            }
                                        }
                                }

                                loadingAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {
                                        Log.i("MYTEST", "animation start")
                                    }
                                    override fun onAnimationEnd(animation: Animator) {
                                        if (bitmaps.size == metadata.size) {
                                            animationWrapper.visibility = View.GONE
                                            placeWrapper.visibility = View.VISIBLE
                                            if (holder.adapterPosition != -1) {
                                                model.bitmapList.add(holder.adapterPosition, bitmaps)
                                            }
                                            else {
                                                Log.i("MYTEST", "NOT ADD TO BITMAPLIST : ${holder.adapterPosition}")
                                            }

                                            recyclerViewImage.adapter = imageAdapter
                                            imageAdapter.submitList(bitmaps)
                                            Log.i("MYTEST", "Bitmap list $bitmaps ")
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
            }


            backButton.setOnClickListener {
                animationWrapper.visibility = View.GONE
                placeWrapper.visibility = View.GONE
                userLocationInput.visibility = View.VISIBLE
                planWrapper.visibility = View.VISIBLE
            }

            holder.notesButton.setOnClickListener {
                userLocationInput.visibility = View.GONE
                planWrapper.visibility = View.GONE
                notesWrapper.visibility = View.VISIBLE
                val checkReservation = viewMap.findViewById<CheckBox>(R.id.reservation_check)
                val noteAddButton = viewMap.findViewById<Button>(R.id.add_note_button)

                val textAreaLayout = viewMap.findViewById<TextInputLayout>(R.id.label_textarea)
                val textArea = viewMap.findViewById<TextInputEditText>(R.id.textarea)

                val userNameLayout = viewMap.findViewById<TextInputLayout>(R.id.user_name_note)
                val userName = viewMap.findViewById<TextInputEditText>(R.id.user_name)

                val dateFromLayout = viewMap.findViewById<TextInputLayout>(R.id.date_from_layout)
                val dateFrom =  viewMap.findViewById<TextInputEditText>(R.id.date_from)

                val dateToLayout = viewMap.findViewById<TextInputLayout>(R.id.date_to_layout)
                val dateTo = viewMap.findViewById<TextInputEditText>(R.id.date_to)

                val paymentLayout = viewMap.findViewById<TextInputLayout>(R.id.value_layout)
                val payment = viewMap.findViewById<TextInputEditText>(R.id.value_of_reservation)

                if (textArea.text!!.isNotBlank()) {
                    textArea.setText("")
                }
                if (userName.text!!.isNotBlank()) {
                    userName.setText("")
                    dateFrom.setText("")
                    dateTo.setText("")
                    payment.setText("")
                }


                checkReservation.setOnCheckedChangeListener{ _, isChecked ->
                    if (isChecked) {
                        textAreaLayout.visibility = View.GONE
                        noteAddButton.marginTop.plus(100)
                        userNameLayout.visibility = View.VISIBLE
                        dateFromLayout.visibility = View.VISIBLE
                        dateToLayout.visibility = View.VISIBLE
                        paymentLayout.visibility = View.VISIBLE
                        dateFrom.focusable = View.NOT_FOCUSABLE
                        dateTo.focusable = View.NOT_FOCUSABLE
                        dateFrom.setOnClickListener {
                            val datePicker = DatePickerDialog(cont)
                            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                                val selectedDate = "${dayOfMonth}/${month+1}/${year}"
                                dateFrom.setText(selectedDate)
                            }
                            datePicker.show()
                        }
                        dateTo.setOnClickListener {
                            val datePicker2 = DatePickerDialog(cont)
                            datePicker2.setOnDateSetListener { _, year, month, dayOfMonth ->
                                val selectedDate2 = "${dayOfMonth}/${month+1}/${year}"
                                dateTo.setText(selectedDate2)
                            }
                            datePicker2.show()
                        }
                    }
                    else {
                        userNameLayout.visibility = View.GONE
                        dateFromLayout.visibility = View.GONE
                        dateToLayout.visibility = View.GONE
                        paymentLayout.visibility = View.GONE
                        textAreaLayout.visibility = View.VISIBLE
                    }
                }

                noteAddButton.setOnClickListener {
                    var noteInfo = ""
                    noteInfo = if (checkReservation.isChecked) {
                        userName.text.toString() + "-" + dateFrom.text.toString() + "-" + dateTo.text.toString() + "-" + payment.text.toString()
                    } else {
                        textArea.text.toString()
                    }
                    model.notes.add(holder.adapterPosition, noteInfo)
                    notesWrapper.visibility = View.GONE
                    userLocationInput.visibility = View.VISIBLE
                    planWrapper.visibility = View.VISIBLE
                }

            }

            backButtonNote.setOnClickListener {
                notesWrapper.visibility = View.GONE
                userLocationInput.visibility = View.VISIBLE
                planWrapper.visibility = View.VISIBLE
            }

        }
        catch(e: NullPointerException) {
            Log.e("MYTEST", "NullPointerException : $e")

            Log.e("MYTEST", "NullPointerException : ${e.stackTrace}")
            Log.e("MYTEST","NullPointerException : ${e.localizedMessage}")
        }
        catch (e: OutOfMemoryError) {
            Log.e("MYTEST", "OutOfMemoryError : $e")

            Log.e("MYTEST", "OutOfMemoryError : ${e.stackTrace}")
            Log.e("MYTEST","OutOfMemoryError : ${e.localizedMessage}")
        }
        catch (e: IllegalStateException) {
            Log.e("MYTEST", "IllegalStateException : $e")

            Log.e("MYTEST", "IllegalStateException : ${e.stackTrace}")
            Log.e("MYTEST","IllegalStateException : ${e.localizedMessage}")
        }
        catch (e: Exception) {
            Log.e("MYTEST", "Exception : $e")

            Log.e("MYTEST", "Exception : ${e.stackTrace}")
            Log.e("MYTEST","Exception : ${e.localizedMessage}")
        }
    }

    fun getNewOrigin(position: Int): String {
        if ((position == -1).or(position > model.newOrigin.size)) {
            throw Exception("List index out of range")
        }
        return model.newOrigin[position]

    }
    
    fun getAllDestinations(): MutableList<String> {
        return model.newOrigin
    }

    fun addPlaceId(placeId: String) {
        model.placeIds.add(placeId)
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


