package com.example.journey_dp.data

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.journey_dp.database.JourneyDao
import com.example.journey_dp.database.model.DestinationItem
import com.example.journey_dp.database.model.JourneyItem
import com.example.journey_dp.database.model.relations.JourneyWithDestinations


class LocalCache(private val dao: JourneyDao) {


    suspend fun deleteJourneys() = dao.deleteJourneys()

    suspend fun deleteAllDestinations() = dao.deleteAllDestinations()

    suspend fun insertDestination(destinationItem: DestinationItem?) {
        dao.insertDestination(destinationItem = destinationItem)
    }

    suspend fun insertJourney(journeyItem: JourneyItem) {
        dao.insertJourney(journeyItem = journeyItem)
    }

    suspend fun deleteDestination(destinationName: String) {
        dao.deleteDestination(destinationName = destinationName)
    }

    suspend fun deleteJourney(journeyName: String) {
        dao.deleteJourney(journeyName = journeyName)
    }

    fun getJourney(journeyName: String) : LiveData<List<JourneyWithDestinations>?> = dao.getJourneyWithDestinations(journeyName = journeyName)

    fun getDestinations(journeyName: String): LiveData<MutableList<DestinationItem?>?> = dao.getDestinations(journeyName = journeyName)
}