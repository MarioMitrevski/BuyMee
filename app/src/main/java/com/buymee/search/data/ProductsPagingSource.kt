package com.buymee.search.data


import androidx.paging.PagingSource
import com.buymee.network.ApiService
import com.buymee.network.ShopProduct
import com.buymee.shops.data.Order
import com.buymee.shops.data.Sort
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class ProductsPagingSource(
    private val service: ApiService,
    private val sort: Sort,
    private val order: Order,
    private val categoryId: Long?
) : PagingSource<Int, ShopProduct>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShopProduct> {
        val position = params.key ?: 0
        return try {
            val products = service.getProducts(position, params.loadSize, sort.name, order.name, categoryId).content
            LoadResult.Page(
                data = products,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (products.isEmpty()) null else position + 1
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
