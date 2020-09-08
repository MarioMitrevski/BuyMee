package com.buymee.shops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buymee.network.ApiService

class ShopsListViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ShopsListViewModel::class.java)) {
            return ShopsListViewModel(apiService) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}