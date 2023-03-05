package com.example.journey_dp.data.room.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// TODO:
//@Entity(tableName = "journeys")
//data class Journey(
//    @PrimaryKey val journeyId: Long,
//    val name: String,
//    val distance: String,
//    val duration: String,
//)
//
//@Entity(tableName = "destinations",
//    foreignKeys = [
//        ForeignKey(entity = Journey::class,
//            parentColumns = ["journeyId"],
//            childColumns = ["journeyId"],
//            onDelete = ForeignKey.CASCADE)
//    ])
//data class Destination(
//    @PrimaryKey val destinationId: Long,
//    val journeyId: Long,
//    val name: String,
//    val note: String
//)
//
//@Entity(tableName = "legs",
//    foreignKeys = [
//        ForeignKey(entity = Destination::class,
//            parentColumns = ["destinationId"],
//            childColumns = ["originId"],
//            onDelete = ForeignKey.CASCADE),
//        ForeignKey(entity = Destination::class,
//            parentColumns = ["destinationId"],
//            childColumns = ["destinationId"],
//            onDelete = ForeignKey.CASCADE)
//    ])
//data class Leg(
//    @PrimaryKey val legId: Long,
//    val originId: Long,
//    val destinationId: Long,
//    val travelMode: String
//)