package com.example.journey_dp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destination")
data class DestinationItem(
    @PrimaryKey(autoGenerate = false)
    val destinationName: String,
    val journeyName: String?,
    val latitude: Double?,
    val longitude: Double?
)