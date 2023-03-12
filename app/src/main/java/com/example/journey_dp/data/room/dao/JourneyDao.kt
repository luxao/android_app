package com.example.journey_dp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(journey: JourneyEntity): Long

    @Update
    suspend fun update(journey: JourneyEntity)

    @Delete
    suspend fun delete(journey: JourneyEntity)

    @Query("SELECT * FROM journey")
    fun getAllJourneys(): LiveData<MutableList<JourneyEntity>>


    @Query("SELECT * FROM journey WHERE id = :journeyId")
    fun getJourneyById(journeyId: Long): Flow<JourneyEntity>


}