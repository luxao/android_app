package com.example.journey_dp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.journey_dp.data.repository.Repository
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.data.room.model.RouteEntity
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository): ViewModel() {

    private val _journeyWithRoutes = MutableLiveData<JourneyWithRoutes>()
    val journeyWithRoutes: LiveData<JourneyWithRoutes>
        get() = _journeyWithRoutes


    fun getJourneyWithRoutesById(journeyId: Long) {
        viewModelScope.launch {
            val journey = repository.getJourneyWithRoutesById(journeyId).asLiveData()
            _journeyWithRoutes.value = journey.value
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