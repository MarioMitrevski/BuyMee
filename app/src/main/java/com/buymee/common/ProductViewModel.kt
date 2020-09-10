package com.buymee.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buymee.network.ApiService
import com.buymee.network.ProductDetailsDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.SocketTimeoutException

class ProductViewModel constructor(
    private val apiService: ApiService
) : ViewModel() {

    private var compositeDisposable = CompositeDisposable()
    private val _liveData = MutableLiveData<ProductFetchState>()
    val liveData: LiveData<ProductFetchState>
        get() = _liveData
    private val _productDTOliveData = MutableLiveData<ProductDetailsDTO>()
    val productDTOliveData: LiveData<ProductDetailsDTO>
        get() = _productDTOliveData


    fun openProduct(productId: String) {
        compositeDisposable.add(apiService.getProduct(
            productId
        )
            .doOnSubscribe { _liveData.postValue(ProductFetchState.Loading) }
            .subscribeOn(Schedulers.io())
            .map {
                if (!it.isSuccessful) {
                    throw Exception("Server error")
                } else {
                    it.body()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _liveData.value = ProductFetchState.ProcessDone }
            .subscribe({
                _productDTOliveData.value = it!!
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
                _liveData.value = ProductFetchState.Error(exception)
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

sealed class ProductFetchState {
    object Loading : ProductFetchState()
    object ProcessDone : ProductFetchState()
    data class Error(val throwable: Throwable) : ProductFetchState()
}