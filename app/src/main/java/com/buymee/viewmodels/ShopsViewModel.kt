package com.buymee.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buymee.model.Shop
import com.buymee.repositories.ShopsRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ShopsViewModel internal constructor(
    var shopsRepository: ShopsRepository
) : ViewModel() {

    val myResponse: MutableLiveData<Response<List<Shop>>> = MutableLiveData()

    fun getShops() {
        viewModelScope.launch {
            val response = shopsRepository.getShops()
            myResponse.value = response
        }
    }
}