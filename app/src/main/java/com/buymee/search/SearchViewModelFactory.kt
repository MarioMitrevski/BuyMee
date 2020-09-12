package com.buymee.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buymee.network.ApiService

class SearchViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(apiService) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}