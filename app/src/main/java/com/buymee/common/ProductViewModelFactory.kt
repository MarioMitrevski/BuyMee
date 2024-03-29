package com.buymee.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buymee.network.ApiService

class ProductViewModelFactory(
    private val apiService: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(apiService, context) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}