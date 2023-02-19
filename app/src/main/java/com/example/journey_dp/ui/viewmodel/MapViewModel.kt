package com.example.journey_dp.ui.viewmodel



import androidx.lifecycle.*

class MapViewModel : ViewModel() {


    private var _isPlaceSet: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPlaceSet: LiveData<Boolean> get() = _isPlaceSet

    private val _placeName: MutableLiveData<String> = MutableLiveData("")
    val placeName: LiveData<String> get() = _placeName

    fun setPlaceName(nameOfPlace: String) {
        _isPlaceSet.value = true
        _placeName.value = nameOfPlace
    }

    fun setValueOfPlace(boolean: Boolean) {
        _isPlaceSet.value = boolean
    }


}