package com.example.journey_dp.data.domain

import androidx.room.Embedded
import androidx.room.Relation
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.RouteEntity

//data class Journey(
//    val id: Long,
//    val name: String,
//    val totalDistance: String,
//    val totalDuration: String,
//    val numberOfDestinations: Int,
//    val sharedUrl: String
//)

data class Journey(
    val journey: JourneyEntity,
    val routes: MutableList<RouteEntity>
)