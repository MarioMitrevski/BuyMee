package com.buymee.shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.buymee.network.ApiService
import com.buymee.network.FeaturedProduct
import com.buymee.network.ShopProduct
import com.buymee.shops.data.Order
import com.buymee.shops.data.ShopProductsPagingSource
import com.buymee.shops.data.Sort
import com.buymee.viewmodels.ProgressLongBackgroundProcess
import kotlinx.coroutines.flow.Flow

class ShopViewModel constructor(
    private val apiService: ApiService
) : ViewModel() {

    lateinit var shopDetails: ShopDetails
    private val _loadingLiveData = MutableLiveData<ProgressLongBackgroundProcess>()
    val loadingLiveData: LiveData<ProgressLongBackgroundProcess>
        get() = _loadingLiveData

    fun loading() {
        _loadingLiveData.value = ProgressLongBackgroundProcess.Loading
    }

    fun processDone() {
        _loadingLiveData.value = ProgressLongBackgroundProcess.ProcessDone
    }

    fun getShopProducts(
        shopId: String,
        sort: Sort,
        order: Order,
        categoryId: Long?
    ): Flow<PagingData<ShopProduct>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                ShopProductsPagingSource(
                    apiService,
                    shopId,
                    sort,
                    order,
                    categoryId
                )
            }
        ).flow.cachedIn(viewModelScope)
    }
}

data class ShopDetails(
    val shopId: String,
    val shopName: String,
    val shopDescription: String,
    val categoryId: Long,
    val createdDate: String,
    val shopLogoImage: String,
    val products: List<FeaturedProduct>
)