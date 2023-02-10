package com.example.journey_dp.data.repository


import androidx.lifecycle.LiveData

import com.example.journey_dp.data.LocalCache
import com.example.journey_dp.database.model.DestinationItem
import com.example.journey_dp.database.model.JourneyItem
import com.example.journey_dp.database.model.relations.JourneyWithDestinations

class AppRepository private constructor(
    private val cache: LocalCache
){


    suspend fun deleteJourneys() {
        cache.deleteJourneys()
    }


    suspend fun deleteAllDestinations() {
        cache.deleteAllDestinations()
    }

    suspend fun deleteJourney(journeyName: String) {
        cache.deleteJourney(journeyName = journeyName)
    }

    suspend fun deleteDestination(destinationName: String) {
        cache.deleteDestination(destinationName = destinationName)
    }

    suspend fun insertJourney(journeyItem: JourneyItem) {
        cache.insertJourney(journeyItem = journeyItem)
    }

    suspend fun insertDestination(destinationItem: DestinationItem?) {
        cache.insertDestination(destinationItem = destinationItem)
    }

    fun getJourney(journeyName: String) : LiveData<List<JourneyWithDestinations>?> {
        return cache.getJourney(journeyName = journeyName)
    }

    fun getDestinations(journeyName: String) : LiveData<MutableList<DestinationItem?>?> {
        return cache.getDestinations(journeyName = journeyName)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(cache: LocalCache): AppRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: AppRepository(cache).also { INSTANCE = it }
            }
    }
}