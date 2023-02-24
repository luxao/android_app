package com.example.journey_dp.data.service

import com.example.journey_dp.data.domain.DirectionsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.*

interface ApiService {


    //https://maps.googleapis.com/maps/api/directions/json?origin=Bratislava&destination=Nitra&mode=driving&key=YOUR_API_KEY
    //https://maps.googleapis.com/maps/api/directions/json?origin=Bratislava&destination=Nitra&mode=walking&key=YOUR_API_KEY
    //https://maps.googleapis.com/maps/api/directions/json?origin=Bratislava&destination=Nitra&mode=transit&transit_mode=bus|train|subway&key=YOUR_API_KEY


    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("transit") transit: String?,
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}