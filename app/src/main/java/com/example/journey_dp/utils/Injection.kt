package com.example.journey_dp.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.data.repository.Repository
import com.example.journey_dp.data.service.ApiService

import com.example.journey_dp.utils.factory.ViewModelFactory


object Injection {
    //TODO: not yet implemented
//    private fun provideCache(context: Context): AppDatabase {
//        val database by lazy { AppDatabase.getDatabase(context)}
//        return database
//    }
//
//    val database = provideCache()

    private fun provideDataRepository(): Repository {
        return Repository.getInstance(ApiService.create())
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository()
        )
    }
}