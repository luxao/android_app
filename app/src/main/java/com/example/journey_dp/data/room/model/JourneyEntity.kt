package com.example.journey_dp.data.room.model

import androidx.room.*


@Entity(tableName = "journey")
data class JourneyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val totalDistance: String,
    val totalDuration: String,
    val numberOfDestinations: Int,
    val sharedUrl: String
)

@Entity(
    tableName = "route",
    foreignKeys = [
        ForeignKey(
            entity = JourneyEntity::class,
            parentColumns = ["id"],
            childColumns = ["journeyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["journeyId"])
    ]
)
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var journeyId: Long,
    val origin: String,
    val destination: String,
    val travelMode: String,
    val note: String
)

data class JourneyWithRoutes(
    @Embedded val journey: JourneyEntity,
    @Relation(
        parentColumn = "journeyId",
        entityColumn = "journey_id"
    )
    val routes: MutableList<RouteEntity>
)