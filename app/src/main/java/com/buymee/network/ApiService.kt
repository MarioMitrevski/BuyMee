package com.buymee.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
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

    @GET("api/products/{product}")
    fun getProduct(
        @Path("product") productId: String
    ): Single<Response<ProductDetailsDTO>>

    @GET("api/categories/all")
    fun getCategories(): Single<Response<List<Category>>>

    @GET("api/products/all")
    suspend fun getProducts(
        @Query("page") page: Int?,
        @Query("page-size") size: Int?,
        @Query("sortBy") sort: String?,
        @Query("order") order: String?,
        @Query("categoryId") categoryId: Long?
    ): ShopProductsResponse

    @POST("/login")
    fun authUser(
        @Body loginRequest: LoginRequest
    ) :Single<Response<ResponseBody>>

    @PUT("/api/cart/addProductItem")
    fun addToCart(
        @Header("Authorization") auth: String,
        @Body addToCart: AddToCartRequest
    ) : Single<Response<ResponseBody>>

    @GET("api/cart")
    fun getCart(
        @Header("Authorization") auth: String
    ) : Single<Response<CartResponse>>

}