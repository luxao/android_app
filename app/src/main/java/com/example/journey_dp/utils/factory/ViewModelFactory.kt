package com.example.journey_dp.utils.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.journey_dp.data.repository.AppRepository
import com.example.journey_dp.ui.viewmodel.InputViewModel
import com.example.journey_dp.ui.viewmodel.SearchViewModel

class ViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(InputViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InputViewModel() as T
        }


        throw IllegalArgumentException("Unknown ViewModel class")
    }
}