package com.example.journey_dp.data.room.dao


import androidx.room.*
import com.example.journey_dp.data.room.model.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(destinations: MutableList<RouteEntity>)

    @Update
    suspend fun update(destination: RouteEntity)

    @Delete
    suspend fun delete(destination: RouteEntity)

    @Query("SELECT * FROM route WHERE journeyId = :journeyId")
    fun getRoutesByJourneyId(journeyId: Long): Flow<MutableList<RouteEntity>>

    @Query("DELETE FROM route WHERE journeyId = :journeyId")
    suspend fun deleteRoutesByJourneyId(journeyId: Long)
}

