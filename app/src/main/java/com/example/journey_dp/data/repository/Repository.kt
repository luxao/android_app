package com.example.journey_dp.data.repository

import android.util.Log
import androidx.lifecycle.*
import com.example.journey_dp.data.domain.ApiResponse
import com.example.journey_dp.data.domain.DirectionsResponse
import com.example.journey_dp.data.domain.Place
import com.example.journey_dp.data.domain.PlaceResponse
import com.example.journey_dp.data.room.AppDatabase
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.JourneyWithRoutes
import com.example.journey_dp.data.room.model.RouteEntity

import com.example.journey_dp.data.service.ApiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Repository private constructor(
    private val service: ApiService,
    private val database: AppDatabase
) {


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


    fun getWikiInfo(title: String, callback: (String) -> Unit, errorCallback: (String) -> Unit) {
        service.getPageIntro(titles = title).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val pages = apiResponse?.query?.pages ?: emptyMap()
                    val page = pages.values.firstOrNull()
                    val extract = page?.extract ?: ""
                    callback(extract)
                } else {
                    errorCallback("Error fetching data from Wikipedia API")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                errorCallback("Error fetching data from Wikipedia API: ${t.message}")
            }
        })
    }

    fun searchNearby(location: LatLng, radius: Int, type: String, key: String,callback: (List<Place>) -> Unit, errorCallback: (String) -> Unit) {
        val locationString = "${location.latitude},${location.longitude}"
        service.getNearbyPlaces(locationString, radius, type, key).enqueue(object : Callback<PlaceResponse> {
            override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                if (response.isSuccessful) {
                     val places = response.body()?.results
                     callback(places!!)
                }
            }

            override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                Log.e("MapsActivity", "Error: ${t.message}")
                errorCallback("Error fetching data from Places API")
            }
        })
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllJourneysWithRoutes(user: String): Flow<MutableList<JourneyWithRoutes>> =
        database.daoJourney().getAllJourneys(user).flatMapLatest { journeys ->
            combine(journeys.map { journey ->
                database.daoJourney().getJourneyById(journey.id).map { newJourney ->
                    JourneyWithRoutes(newJourney, database.daoRoute().getRoutesByJourneyId(newJourney.id).firstOrNull() ?: mutableListOf())
                }
            }) { results ->
                results.toMutableList()
            }
        }.flowOn(Dispatchers.IO)



    fun getAllJourneys(user:String): Flow<MutableList<JourneyEntity>> = database.daoJourney().getAllJourneys(user)


    suspend fun insertJourneyAndRoutes(journey: JourneyEntity, routes: MutableList<RouteEntity>) {
        withContext(Dispatchers.IO) {
            database.daoJourney().insert(journey).also { journeyId ->
                routes.forEach { it.journeyId = journeyId }
                database.daoRoute().insert(routes)
            }
        }
    }

    suspend fun updateJourneyAndRoutes(journey: JourneyEntity, routes: MutableList<RouteEntity>) {
        withContext(Dispatchers.IO) {
            database.daoJourney().update(journey)
            database.daoRoute().insert(routes)
        }
    }

    suspend fun deleteJourneyAndRoutes(journey: JourneyEntity) {
        withContext(Dispatchers.IO) {
            database.daoRoute().deleteRoutesByJourneyId(journey.id)
            database.daoJourney().delete(journey)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getJourneyWithRoutesById(journeyId: Long): Flow<JourneyWithRoutes> =
        database.daoJourney().getJourneyById(journeyId).flatMapLatest { journey ->
            database.daoRoute().getRoutesByJourneyId(journeyId).map { routes ->
                JourneyWithRoutes(journey, routes)
            }
        }




    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(service: ApiService, database: AppDatabase): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Repository(service,database).also { INSTANCE = it }
            }
    }
}