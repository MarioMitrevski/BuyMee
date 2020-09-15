package com.buymee.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buymee.network.ApiService

class UserViewModelFactory(
    private val apiService: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(apiService, context) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}