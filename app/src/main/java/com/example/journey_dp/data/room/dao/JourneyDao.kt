package com.example.journey_dp.data.room.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(journey: JourneyEntity): Long
//
//    @Update
//    suspend fun update(journey: JourneyEntity)
//
//    @Delete
//    suspend fun delete(journey: JourneyEntity)
//
//    @Query("SELECT * FROM JourneyEntity")
//    fun getAllJourneys(): Flow<List<JourneyEntity>>
//
//    @Query("SELECT * FROM JourneyEntity WHERE journeyId = :journeyId")
//    fun getJourneyById(journeyId: Long): Flow<JourneyEntity>
}