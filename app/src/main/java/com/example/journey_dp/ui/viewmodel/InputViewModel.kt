package com.example.journey_dp.ui.viewmodel


import android.view.View
import androidx.lifecycle.*
import com.example.journey_dp.domain.InputData
import com.example.journey_dp.ui.adapter.adapters.InputAdapter
import com.google.android.material.textfield.TextInputEditText


class InputViewModel : ViewModel() {

    private var _isPlaceSet: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPlaceSet: LiveData<Boolean> get() = _isPlaceSet

    private val _placeName: MutableLiveData<String> = MutableLiveData("")
    val placeName: LiveData<String> get() = _placeName


    fun setPlaceName(nameOfPlace: String) {
        _isPlaceSet.postValue(true)
        _placeName.value = nameOfPlace
    }

    fun setValue(boolean: Boolean) {
        _isPlaceSet.postValue(boolean)
    }


}