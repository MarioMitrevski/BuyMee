package com.buymee.shops.data

import androidx.paging.PagingSource
import com.buymee.network.ApiService
import com.buymee.network.Shop
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class ShopsPagingSource(
    private val service: ApiService,
    private val query: String
) : PagingSource<Int, Shop>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Shop> {
        val position = params.key ?: 0
        return try {
            val shops = service.getShops(query, position, params.loadSize).content
            LoadResult.Page(
                data = shops,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (shops.isEmpty()) null else position + 1
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