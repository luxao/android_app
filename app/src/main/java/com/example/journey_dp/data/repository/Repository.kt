package com.example.journey_dp.data.repository

import android.util.Log
import com.example.journey_dp.data.domain.DirectionsResponse
import com.example.journey_dp.data.domain.Route
import com.example.journey_dp.data.service.ApiService
import java.io.IOException

class Repository private constructor(
    private val service: ApiService
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