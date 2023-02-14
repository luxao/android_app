package com.example.journey_dp.database


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.journey_dp.database.model.DestinationItem
import com.example.journey_dp.database.model.JourneyItem
import com.example.journey_dp.database.model.relations.JourneyWithDestinations

@Dao
interface JourneyDao {

    @Query("DELETE FROM JOURNEY")
    suspend fun deleteJourneys()

    @Query("DELETE FROM destination")
    suspend fun deleteAllDestinations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(destinationItem: DestinationItem?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(journeyItem: JourneyItem)

    @Query("DELETE FROM destination WHERE destinationName = :destinationName")
    suspend fun deleteDestination(destinationName: String)

    @Transaction
    @Query("DELETE FROM journey WHERE journeyName = :journeyName")
    suspend fun deleteJourney(journeyName: String)

    @Query("SELECT * FROM destination WHERE journeyName = :journeyName")
    fun getDestinations(journeyName: String): LiveData<MutableList<DestinationItem?>?>


    @Transaction
    @Query("SELECT * FROM journey WHERE journeyName = :journeyName")
    fun getJourneyWithDestinations(journeyName: String): LiveData<List<JourneyWithDestinations>?>

}