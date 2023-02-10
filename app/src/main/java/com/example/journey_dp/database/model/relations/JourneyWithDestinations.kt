package com.example.journey_dp.database.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.journey_dp.database.model.DestinationItem
import com.example.journey_dp.database.model.JourneyItem

data class JourneyWithDestinations(
    @Embedded val journeyItem: JourneyItem,
    @Relation(
        parentColumn = "journeyName",
        entityColumn = "journeyName"
    )
    val destinations: List<DestinationItem>
)