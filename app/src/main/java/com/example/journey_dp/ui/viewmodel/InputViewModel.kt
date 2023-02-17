package com.example.journey_dp.ui.viewmodel


import android.view.View
import androidx.lifecycle.*
import com.example.journey_dp.domain.InputData


class InputViewModel : ViewModel() {

    //TODO:
    // Spravit menitelnu premenu, ktorej sa nastavi Place Name
    // Spravit funkciu ktora nastavi novu hodnotu premenej s Place Name
    // Ziskanie hodnoty premenej na nastevenie hodnoty do inputu

    private val _placeName: MutableLiveData<String> = MutableLiveData("")
    val placeName: LiveData<String> get() = _placeName

    fun setPlaceName(nameOfPlace: String) {
        _placeName.value = nameOfPlace
    }

}