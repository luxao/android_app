package com.example.journey_dp.data.domain

//TODO: FIX DATA CLASSES !!!!

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>,
    val overviewPolyline: Polyline
)

//TODO: NEED UPGRADE
data class Leg(
    val steps: List<Step>,
    val duration: DistanceDuration,
    val distance: DistanceDuration,
    // additional fields for transit mode
    val arrivalTime: Long?,
    val departureTime: Long?,
    val headsign: String?,
    val line: TransitLine?,
    // additional fields for driving mode
    val startAddress: String?,
    val endAddress: String?,
    val startLocation: LatLng?,
    val endLocation: LatLng?
)

data class Step(
    val distance: DistanceDuration,
    val duration: DistanceDuration,
    val startLocation: LatLng,
    val endLocation: LatLng,
    val polyline: Polyline,
    val travelMode: String,
    // additional fields for transit mode
    val transitDetails: TransitDetails?,
    // additional fields for driving mode
    val maneuver: String?
)

data class DistanceDuration(
    val value: Int,
    val text: String
)

data class Polyline(
    val points: String
)

data class TransitLine(
    val name: String?,
    val shortName: String?,
    val color: String?,
    val textColor: String?
)

data class TransitDetails(
    val arrivalStop: TransitStop?,
    val departureStop: TransitStop?,
    val arrivalTime: Long?,
    val departureTime: Long?,
    val headsign: String?,
    val line: TransitLine?,
    val numStops: Int
)

data class TransitStop(
    val name: String?,
    val location: LatLng?
)

data class LatLng(
    val lat: Double,
    val lng: Double
)


