package com.example.journey_dp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.journey_dp.data.room.dao.JourneyDao
import com.example.journey_dp.data.room.dao.RouteDao
import com.example.journey_dp.data.room.model.JourneyEntity
import com.example.journey_dp.data.room.model.RouteEntity


@Database(entities = [JourneyEntity::class, RouteEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun daoJourney(): JourneyDao
    abstract fun daoRoute(): RouteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "journey_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}