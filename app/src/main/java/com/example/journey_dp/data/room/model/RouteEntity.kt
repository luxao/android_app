package com.example.journey_dp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val origin: String,
    val destination: String
)