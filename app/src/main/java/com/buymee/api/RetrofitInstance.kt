package com.buymee.api

import com.buymee.api.ShopService
import com.buymee.utilities.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ShopService by lazy {

        retrofit.create(ShopService::class.java)
    }
}