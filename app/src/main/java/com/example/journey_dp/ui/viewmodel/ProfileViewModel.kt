package com.example.journey_dp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.journey_dp.data.repository.Repository
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.data.room.model.RouteEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository): ViewModel() {

    val journeyId = MutableLiveData<Long>()

    val journeyWithRoutes: LiveData<JourneyWithRoutes> = journeyId.switchMap {
        liveData {
            it?.let {
                val journey = repository.getJourneyWithRoutesById(it)
                emit(journey.first())
            }
        }
    }


    val journeysWithDestinations: LiveData<MutableList<JourneyWithRoutes>> =
        repository.getAllJourneys().asLiveData()

    fun insertJourneyWithDestinations(journey: JourneyEntity, routes: MutableList<RouteEntity>) {
        viewModelScope.launch {
            repository.insertJourneyAndRoutes(journey, routes)
        }
    }

    fun updateJourneyWithDestinations(journey: JourneyEntity, routes: MutableList<RouteEntity>) {
        viewModelScope.launch {
            repository.updateJourneyAndRoutes(journey, routes)
        }
    }

    fun deleteJourneyWithDestinations(journey: JourneyEntity) {
        viewModelScope.launch {
            repository.deleteJourneyAndRoutes(journey)
        }
    }




}