package com.example.journey_dp.utils.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.data.repository.Repository

import com.example.journey_dp.ui.viewmodel.MapViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }


        throw IllegalArgumentException("Unknown ViewModel class")
    }
}