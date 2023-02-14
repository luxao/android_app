package com.example.journey_dp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place

class MapViewModel : ViewModel() {


    private var _isPlaceSet: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPlaceSet: LiveData<Boolean>
        get() = _isPlaceSet

    private var _isStatusSet: MutableLiveData<Boolean> = MutableLiveData(false)
    val isStatusSet: LiveData<Boolean>
        get() = _isStatusSet

    private lateinit var _placeFromSearch: MutableLiveData<Place>
    val placeFromSearch: LiveData<Place>
        get() = _placeFromSearch

    private lateinit var _status: MutableLiveData<Status>
    val status: LiveData<Status>
        get() = _status



    fun setIsPlaceSet(place: Place) {
        _isPlaceSet.value = _isPlaceSet.value!!.not()
        _placeFromSearch.postValue(place)
    }


}