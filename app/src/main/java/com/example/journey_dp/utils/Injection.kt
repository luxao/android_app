package com.example.journey_dp.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.data.LocalCache
import com.example.journey_dp.data.network.RestApi
import com.example.journey_dp.data.repository.AppRepository
import com.example.journey_dp.database.AppDatabase
import com.example.journey_dp.utils.factory.ViewModelFactory


object Injection {
    private fun provideCache(context: Context): LocalCache {
        val database = AppDatabase.getDatabase(context)
        return LocalCache(database.dao())
    }

    fun provideDataRepository(context: Context): AppRepository {
        return AppRepository.getInstance(provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(
                context
            )
        )
    }
}