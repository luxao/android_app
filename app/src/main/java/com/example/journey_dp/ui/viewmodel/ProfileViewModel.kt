package com.example.journey_dp.ui.viewmodel


import androidx.lifecycle.*
import com.example.journey_dp.data.repository.Repository
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.data.room.model.RouteEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository, private val auth: FirebaseAuth): ViewModel() {

    private val userEmail = auth.currentUser!!.email
    val journeyId = MutableLiveData<Long>()

    var helperJourney = mutableListOf<JourneyEntity>()

    val journeyWithRoutes: LiveData<JourneyWithRoutes> = journeyId.switchMap {
        liveData {
            it?.let { it ->
                val journey = repository.getJourneyWithRoutesById(it)
                journey.first().let { value ->
                    emit(value)
                }
            }
        }
    }

    val journeysWithDestinations: LiveData<MutableList<JourneyWithRoutes>> =
        repository.getAllJourneysWithRoutes(userEmail.toString()).asLiveData()


    val journeys: LiveData<MutableList<JourneyEntity>> = repository.getAllJourneys(userEmail.toString()).asLiveData()



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

    fun refreshData() {
        repository.getAllJourneysWithRoutes(userEmail.toString())
    }




}