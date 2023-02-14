package com.example.journey_dp.ui.viewmodel


import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*

import com.example.journey_dp.data.repository.AppRepository
import com.example.journey_dp.database.model.DestinationItem
import com.example.journey_dp.database.model.JourneyItem
import com.example.journey_dp.database.model.relations.JourneyWithDestinations
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.launch


class SearchViewModel(private val repository: AppRepository): ViewModel() {



    private var _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: MutableLiveData<Boolean> get() = _loading

    private var _journeyName: MutableLiveData<String> = MutableLiveData("")

    fun setJourneyName(name: String) {
        _journeyName.value = name
    }

    fun deleteJourneys() {
        viewModelScope.launch {
            repository.deleteJourneys()
        }
    }

    fun deleteDestinations() {
        viewModelScope.launch {
            repository.deleteAllDestinations()
        }
    }

    var destinations: LiveData<MutableList<DestinationItem?>?> =
        liveData {
            _loading.postValue(true)
            repository.getDestinations(_journeyName.toString())
            _loading.postValue(false)
        }

    fun getJourney(journeyName: String): LiveData<List<JourneyWithDestinations>?> {
        return liveData {
            _loading.postValue(true)
            repository.getJourney(journeyName = journeyName)
            _loading.postValue(false)
        }
    }

    fun addDestination(destinationItem: DestinationItem) {
        viewModelScope.launch {
            _loading.postValue(true)
            repository.insertDestination(destinationItem = destinationItem)
            _loading.postValue(false)
        }
    }


    fun addJourney(journey: JourneyItem) {
        viewModelScope.launch {
            _loading.postValue(true)
            repository.insertJourney(journeyItem = journey)
            _loading.postValue(false)
        }
    }
}