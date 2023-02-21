package com.example.journey_dp.data.repository

import com.example.journey_dp.data.service.ApiService

class Repository private constructor(
    private val service: ApiService
) {

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(service: ApiService): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Repository(service).also { INSTANCE = it }
            }
    }
}