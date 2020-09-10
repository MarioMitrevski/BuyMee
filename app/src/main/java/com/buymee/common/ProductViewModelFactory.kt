package com.buymee.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buymee.network.ApiService

class ProductViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(apiService) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}