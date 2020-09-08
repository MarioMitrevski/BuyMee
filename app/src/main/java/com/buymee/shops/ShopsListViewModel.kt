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
import com.buymee.network.Shop
import com.buymee.network.ShopDetailsDTO
import com.buymee.shops.data.ShopsPagingSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*

class ShopsListViewModel constructor(private val apiService: ApiService) : ViewModel() {

    private var currentQueryValue: String? = null
    private var currentSearchResult: Flow<PagingData<Shop>>? = null
    private var compositeDisposable = CompositeDisposable()
    private val _liveData = MutableLiveData<ShopFetchState>()
    val liveData: LiveData<ShopFetchState>
        get() = _liveData

    fun searchShop(queryString: String): Flow<PagingData<Shop>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<Shop>> = Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                ShopsPagingSource(
                    apiService,
                    queryString
                )
            }
        ).flow.cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun openShop(shopId: UUID) {
        compositeDisposable.add(apiService.getShop(
            shopId
        )
            .doOnSubscribe { _liveData.postValue(ShopFetchState.Loading) }
            .subscribeOn(Schedulers.io())
            .map {
                if (!it.isSuccessful) {
                    throw Exception("Server error")
                } else {
                    it.body()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _liveData.value = ShopFetchState.ProcessDone }
            .subscribe({
                _liveData.value = ShopFetchState.ShopFetched(it!!)
            }, {
                val exception =
                    when (it) {
                        is SocketTimeoutException -> Exception("Socket timeout exception")
                        is IOException -> Exception("No Internet connection")
                        else -> {
                            it.message
                            Exception( "Something went wrong")
                        }
                    }
                _liveData.value = ShopFetchState.Error(exception)
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

sealed class ShopFetchState {
    object Loading : ShopFetchState()
    object ProcessDone : ShopFetchState()
    data class ShopFetched(val shopDetailsDTO: ShopDetailsDTO) : ShopFetchState()
    data class Error(val throwable: Throwable) : ShopFetchState()
}