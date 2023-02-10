package com.example.journey_dp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journey")
data class JourneyItem(
    @PrimaryKey(autoGenerate = false)
    val journeyName: String
)
