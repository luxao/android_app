package com.example.journey_dp.data.domain

import com.google.gson.annotations.SerializedName


data class DirectionsResponse(
    val routes: List<Route>?,
    val status: String?
)


data class Route(
    val legs: List<Leg>,
    @SerializedName("overview_polyline")
    val overviewPolyline: Polyline,
    val summary: String?,
    val warnings: List<String>?
)

data class Leg(
    val distance: DistanceDuration,
    val duration: DistanceDuration,

    @SerializedName("end_address")
    val endAddress: String?,

    @SerializedName("end_location")
    val endLocation: Coordinates?,

    @SerializedName("start_address")
    val startAddress: String?,

    @SerializedName("start_location")
    val startLocation: Coordinates?,

    val steps: List<Step>,

    @SerializedName("arrival_time")
    val arrivalTime: ArrivalDepartureTime?,

    @SerializedName("departure_time")
    val departureTime: ArrivalDepartureTime?,

    @SerializedName("headsign")
    val headSign: String?,

    val line: TransitLine?

)

data class Step(
    val distance: DistanceDuration,
    val duration: DistanceDuration,

    @SerializedName("end_location")
    val endLocation: Coordinates?,

    @SerializedName("html_instructions")
    val instructions: String?,

    @SerializedName("start_location")
    val startLocation: Coordinates?,

    @SerializedName("travel_mode")
    val travelMode: String,

    val polyline: Polyline,

    val maneuver: String?,

    @SerializedName("transit_details")
    val transitDetails: TransitDetails?
)

data class TransitDetails(
    @SerializedName("arrival_stop")
    val arrivalStop: ArrivalDepartureStop?,

    @SerializedName("arrival_time")
    val arrivalTime: ArrivalDepartureTime?,

    @SerializedName("departure_stop")
    val departureStop: ArrivalDepartureStop?,

    @SerializedName("departure_time")
    val departureTime: ArrivalDepartureTime?,

    @SerializedName("headsign")
    val headSign: String?,

    val line: TransitLine?,

    @SerializedName("num_stops")
    val numStops: Int?,

    @SerializedName("trip_short_name")
    val tripShortName: String?

)

data class ArrivalDepartureStop(
    val location: Coordinates?,
    val name: String?
)

data class TransitLine(
    val agencies: List<Agencies>?,
    val color: String?,

    @SerializedName("text_color")
    val textColor: String?,

    val vehicle: Vehicle?
)

data class Vehicle(
    val icon: String?,
    val name: String?,
    val type: String?
)

data class Agencies(
    val name: String?,
    val phone: String?,
    val url: String?
)

data class ArrivalDepartureTime(
    val text: String?,
    @SerializedName("time_zone")
    val timeZone: String?,
    val value: Long?
)

data class DistanceDuration(
    val text: String,
    val value: Int
)

data class Polyline(
    val points: String
)

data class Coordinates(
    val lat: Double,
    val lng: Double
)


