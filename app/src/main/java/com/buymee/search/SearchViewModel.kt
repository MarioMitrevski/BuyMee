package com.buymee.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.buymee.network.ApiService
import com.buymee.network.Category
import com.buymee.network.ShopProduct
import com.buymee.search.data.ProductsPagingSource
import com.buymee.shops.data.Order
import com.buymee.shops.data.Sort
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.net.SocketTimeoutException

class SearchViewModel constructor(
    private val apiService: ApiService
) : ViewModel() {

    private var compositeDisposable = CompositeDisposable()
    private val _liveData = MutableLiveData<NetworkFetchState>()
    val liveData: LiveData<NetworkFetchState>
        get() = _liveData
    lateinit var categories: List<Category>
    var categoryIdClicked: Long = -1L
    var categoryNoChildrenClicked = false

    fun getCategories() {
        compositeDisposable.add(apiService.getCategories()
            .doOnSubscribe { _liveData.postValue(NetworkFetchState.Loading) }
            .subscribeOn(Schedulers.io())
            .map {
                if (!it.isSuccessful) {
                    throw Exception("Server error")
                } else {
                    it.body()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _liveData.value = NetworkFetchState.ProcessDone }
            .subscribe({
                categories = it!!.toList()
                _liveData.value = NetworkFetchState.Fetched(it)
            }, {
                val exception =
                    when (it) {
                        is SocketTimeoutException -> Exception("Socket timeout exception")
                        is IOException -> Exception("No Internet connection")
                        else -> {
                            it.message
                            Exception("Something went wrong")
                        }
                    }
                _liveData.value = NetworkFetchState.Error(exception)
            })
        )
    }

    fun getProducts(
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
                ProductsPagingSource(
                    apiService,
                    sort,
                    order,
                    categoryId
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

sealed class NetworkFetchState {
    object Loading : NetworkFetchState()
    object ProcessDone : NetworkFetchState()
    data class Fetched(val categories: List<Category>) : NetworkFetchState()
    data class Error(val throwable: Throwable) : NetworkFetchState()
}