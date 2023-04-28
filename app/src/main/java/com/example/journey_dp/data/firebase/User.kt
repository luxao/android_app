package com.example.journey_dp.data.firebase

data class User(
    val userName: String,
    val userEmail: String,
    val userImage: String
)
{
    constructor(): this("","","")
}

data class UserWithUID (
    val userId: String = "",
    val userEmail: String = "",
    val userImage: String = "",
    val userName: String = ""
    ) {
    constructor(): this("","","","")
}

