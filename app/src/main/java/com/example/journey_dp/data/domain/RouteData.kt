package com.example.journey_dp.data.domain



data class RouteData(
    val id: Long,
    val journeyId: Long,
    val origin: String,
    val destination: String,
    val travelMode: String,
    val note: String?
)
