package com.example.journey_dp.data.repository

import android.util.Log
import com.example.journey_dp.data.domain.DirectionsResponse
import com.example.journey_dp.data.domain.Route
import com.example.journey_dp.data.room.dao.JourneyDao
import com.example.journey_dp.data.room.dao.RouteDao

import com.example.journey_dp.data.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.io.IOException

class Repository private constructor(
    private val service: ApiService
) {

//    private val journeyDao: JourneyDao,
//    private val routeDao: RouteDao,

    suspend fun getDirections(
        origin: String,
        destination: String,
        mode: String,
        transit: String,
        key: String,
        onError: (error: String) -> Unit
    ) : DirectionsResponse? {
        var directions: DirectionsResponse? = null
        try {
            val resp = service.getDirections(origin,destination,mode,transit,key)
            if (resp.isSuccessful) {
                resp.body()?.let { data ->
                    directions = data
                    Log.i("MYTEST", "Data: $data")
                }?:  Log.i("MYTEST","Failed to load directions")
            }
            else {
                Log.i("MYTEST","Failed to read directions")
                onError("Failed to read directions")
            }
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            Log.i("MYTEST","Failed to load directions, check internet connection")
            onError("Failed to load directions, check internet connection")
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            Log.i("MYTEST","Failed to load directions, error")
            onError("Failed to load directions, error")
        }
        return directions
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    fun getAllJourneys(): Flow<List<JourneyWithRoutes>> =
//        journeyDao.getAllJourneys().flatMapLatest { journeys ->
//            combine(journeys.map { journey ->
//                journeyDao.getJourneyById(journey.id).map { newJourney ->
//                    JourneyWithRoutes(newJourney, routeDao.getRoutesByJourneyId(newJourney.id).firstOrNull() ?: emptyList())
//                }
//            }) { results ->
//                results.toList()
//            }
//        }.flowOn(Dispatchers.Default)
//
//    suspend fun insertJourneyAndRoutes(journey: JourneyEntity, routes: List<RouteEntity>) {
//        journeyDao.insert(journey).also { journeyId ->
//            routes.forEach { it.journeyId = journeyId }
//            routeDao.insert(routes)
//        }
//    }
//
//    suspend fun updateJourneyAndRoutes(journey: JourneyEntity, routes: List<RouteEntity>) {
//        journeyDao.update(journey)
//        routeDao.insert(routes)
//    }
//
//    suspend fun deleteJourneyAndRoutes(journey: JourneyEntity) {
//        routeDao.deleteRoutesByJourneyId(journey.id)
//        journeyDao.delete(journey)
//    }


    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(service: ApiService): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Repository(service).also { INSTANCE = it }
            }
    }
}