package com.buymee.network

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface ApiService {

    @GET("api/shops/allShops")
    suspend fun getShops(
        @Query("q") query: String,
        @Query("page") page: Int?,
        @Query("page-size") itemsPerPage: Int?
    ): ShopList

    @GET("api/shops/{shopId}")
    fun getShop(
        @Path("shopId") shopId: UUID
    ): Single<Response<ShopDetailsDTO>>

    @GET("api/products/shop/{shopId}")
    suspend fun getShopProducts(
        @Path("shopId") shopId: UUID,
        @Query("page") page: Int?,
        @Query("page-size") size: Int?,
        @Query("sortBy") sort: String?,
        @Query("order") order: String?,
        @Query("categoryId") categoryId: Long?
    ): ShopProductsResponse
}