package com.example.journey_dp.data.domain

data class PlaceResponse(
    val results: List<Place>
)

data class Place(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Geometry(
    val location: Location
)