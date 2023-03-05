package com.example.journey_dp.data.room.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(destinations: List<RouteEntity>)
//
//    @Update
//    suspend fun update(destination: RouteEntity)
//
//    @Delete
//    suspend fun delete(destination: RouteEntity)
//
//    @Query("SELECT * FROM DestinationEntity WHERE journeyId = :journeyId")
//    fun getRoutesByJourneyId(journeyId: Long): Flow<List<RouteEntity>>
//
//    @Query("DELETE FROM destinations WHERE journeyId = :journeyId")
//    suspend fun deleteRoutesByJourneyId(journeyId: Long)
}