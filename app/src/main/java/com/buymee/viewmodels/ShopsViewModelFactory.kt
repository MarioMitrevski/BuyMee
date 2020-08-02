package com.buymee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buymee.repositories.ShopsRepository

class ShopsViewModelFactory(
    private val repository: ShopsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(ShopsViewModel::class.java)){
            return ShopsViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}