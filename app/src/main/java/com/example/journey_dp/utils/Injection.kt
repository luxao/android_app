package com.example.journey_dp.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.data.repository.Repository
import com.example.journey_dp.data.room.AppDatabase
import com.example.journey_dp.data.service.ApiService

import com.example.journey_dp.utils.factory.ViewModelFactory


object Injection {

    private fun provideCache(context: Context): AppDatabase {
        val database by lazy { AppDatabase.getDatabase(context)}
        return database
    }
    
    private fun provideDataRepository(context: Context): Repository {
        return Repository.getInstance(ApiService.create(),provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(context)
        )
    }
}