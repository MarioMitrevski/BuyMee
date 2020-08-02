package com.buymee.repositories

import android.content.Context
import com.buymee.api.RetrofitInstance
import com.buymee.model.Shop

import retrofit2.Response

class ShopsRepository private constructor(
    private val context: Context
) {

    suspend fun getShops(): Response<List<Shop>>{
        return RetrofitInstance.api.getShops()
    }

    companion object {
        @Volatile
        private var INSTANCE: ShopsRepository? = null
        fun getInstance(context: Context): ShopsRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ShopsRepository(context).also {
                    INSTANCE = it
                }
            }
        private const val TAG = "ShopsRepository"
    }
}