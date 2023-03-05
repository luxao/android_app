package com.example.journey_dp.data.domain

data class Journey(
    val id: Long,
    val name: String,
    val totalDistance: Double,
    val totalDuration: Double,
    val sharedUrl: String
)
