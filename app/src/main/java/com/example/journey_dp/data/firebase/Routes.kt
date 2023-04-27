package com.example.journey_dp.data.firebase

data class Routes(
    val id: Long?,
    var journeyId: Long?,
    val origin: String?,
    val destination: String?,
    val travelMode: String?,
    val note: String?,
    val originName: String?,
    val destinationName: String?
) {
    constructor() : this(0L, 0L, "","","","","","")
}
