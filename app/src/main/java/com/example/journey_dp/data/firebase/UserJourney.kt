package com.example.journey_dp.data.firebase

class UserJourney(
    val name: String,
    val id: Long?,
    val user: String?,
    val totalDistance: String?,
    val totalDuration: String?,
    val numberOfDestinations: Int?,
    val routes: List<Routes>?,
    var sharedUrl: String?
) {
    constructor(): this("", 0L, "","","",0,null,"")
}

