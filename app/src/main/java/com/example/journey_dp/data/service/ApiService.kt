package com.example.journey_dp.data.service

import com.example.journey_dp.data.domain.ApiResponse
import com.example.journey_dp.data.domain.DirectionsResponse
import com.example.journey_dp.data.domain.PlaceResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.*

interface ApiService {

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("transit_mode") transit: String,
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>

    @GET("https://en.wikipedia.org/w/api.php")
    @Headers(
        "Content-Type: application/json; charset=utf-8"
    )
    fun getPageIntro(
        @Query("action") action: String = "query",
        @Query("prop") prop: String = "extracts",
        @Query("format") format: String = "json",
        @Query("titles") titles: String,
        @Query("exintro") exintro: Boolean = true,
        @Query("explaintext") explaintext: Boolean = true,
        @Query("exsentences") exsentences: Int = 5
    ): Call<ApiResponse>

    @GET("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): Call<PlaceResponse>

//    https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=48.14838109999999,%2017.1080601&radius=5000&type=restaurant&key=AIzaSyCO6xmxS7BzhcUdNJE0zx5usSuL25of2ic
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