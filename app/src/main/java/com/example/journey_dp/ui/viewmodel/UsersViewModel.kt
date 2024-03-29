package com.example.journey_dp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.journey_dp.data.firebase.UserJourney
import com.example.journey_dp.data.firebase.UserWithUID
import com.example.journey_dp.data.repository.Repository
import com.google.firebase.auth.FirebaseAuth

class UsersViewModel(private val repository: Repository, private val auth: FirebaseAuth): ViewModel() {

    var userName : String = ""
    var allUsers = mutableListOf<UserWithUID>()
    var searchedUsers = mutableListOf<UserWithUID>()
    var loggedUser = UserWithUID()
    var requestedUsers = mutableListOf<String>()
    var requestsList = mutableListOf<UserWithUID>()
    var followingUsers = mutableListOf<UserWithUID>()
    var userJourneys = mutableListOf<UserJourney>()

}