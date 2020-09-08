package com.buymee.shops.data

import androidx.paging.PagingSource
import com.buymee.network.ApiService
import com.buymee.network.ShopProduct
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*

class ShopProductsPagingSource(
    private val service: ApiService,
    private val shopId: String,
    private val sort: Sort,
    private val order: Order,
    private val categoryId: Long?
) : PagingSource<Int, ShopProduct>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShopProduct> {
        val position = params.key ?: 0
        return try {
            val shopProducts = service.getShopProducts(UUID.fromString(shopId), position, params.loadSize, sort.name, order.name, categoryId).content
            LoadResult.Page(
                data = shopProducts,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (shopProducts.isEmpty()) null else position + 1
            )
        } catch (exception: SocketTimeoutException) {
            return LoadResult.Error(Exception("Something went wrong"))
        } catch (exception: SocketException) {
            return LoadResult.Error(Exception("Something went wrong"))
        } catch (exception: IOException) {
            return LoadResult.Error(Exception("No internet connection"))
        } catch (exception: HttpException) {
            return LoadResult.Error(Exception("Server error"))
        }
    }
}

enum class Order{
    DESC, ASC
}

enum class Sort{
    created_date, price
}