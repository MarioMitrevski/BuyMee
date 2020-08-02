package com.buymee.api

import com.buymee.model.Shop
import retrofit2.Response
import retrofit2.http.GET

interface ShopService {

    @GET("api/shops/allShops")
    suspend fun getShops() : Response<List<Shop>>
}