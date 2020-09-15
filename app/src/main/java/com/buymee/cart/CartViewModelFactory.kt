package com.buymee.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buymee.network.ApiService

class CartViewModelFactory(
    private val apiService: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(apiService, context) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}