package com.buymee.common

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buymee.data.UserPrefs
import com.buymee.network.AddToCartRequest
import com.buymee.network.ApiService
import com.buymee.network.LoginRequest
import com.buymee.network.ProductDetailsDTO
import com.buymee.user.LoginState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.SocketTimeoutException

class ProductViewModel constructor(
    private val apiService: ApiService,
    private val context: Context
) : ViewModel() {

    private var compositeDisposable = CompositeDisposable()
    var selectedProductItemId: String = ""
    var selectedQuantity: String = ""

    private val _liveData = MutableLiveData<ProductFetchState>()
    val liveData: LiveData<ProductFetchState>
        get() = _liveData

    private val _cartLiveData = MutableLiveData<AddToCartState>()
    val cartLiveData: LiveData<AddToCartState>
        get() = _cartLiveData

    private val _productDTOliveData = MutableLiveData<ProductDetailsDTO>()
    val productDTOliveData: LiveData<ProductDetailsDTO>
        get() = _productDTOliveData

    private val _loginLiveData = MutableLiveData<LoginState>()
    val loginLiveData: LiveData<LoginState>
        get() = _loginLiveData

    fun loginUser(email: String, password: String){
        compositeDisposable.add(apiService.authUser(LoginRequest(email, password))
            .doOnSubscribe { _loginLiveData.postValue(LoginState.Loading) }
            .subscribeOn(Schedulers.io())
            .map {
                when {
                    it.isSuccessful -> {
                        UserPrefs.getInstance(context).storeToken(it.headers()["Authorization"]!!)
                    }
                    it.code() in 400..500 -> throw Exception("Bad credentials")
                    else -> throw Exception("Login failed")
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                _loginLiveData.value =
                    LoginState.ProcessDone
            }
            .subscribe({
                _loginLiveData.value = LoginState.SuccessfulLogin
            }, {
                UserPrefs.getInstance(context).logout()
                val exception =
                    when (it) {
                        is SocketTimeoutException -> Exception("Socket timeout exception")
                        is IOException -> Exception("No Internet connection")
                        else -> {
                            Exception(it.message ?: "Something went wrong")
                        }
                    }
                _loginLiveData.value = LoginState.Error(exception)
            })
        )
    }

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
                it!!.productItems = it.productItems.sortedBy { pi -> pi.price }.toList()
                _productDTOliveData.value = it
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

    fun addToCart(poductItemId: String, quantity: String) {
        compositeDisposable.add(apiService.addToCart(
            UserPrefs.getInstance(context).getToken() ?: "",
            AddToCartRequest(
                poductItemId,
                quantity
            )
        )
            .doOnSubscribe { _cartLiveData.postValue(AddToCartState.Loading) }
            .subscribeOn(Schedulers.io())
            .map {
                when {
                    it.isSuccessful -> it.body()
                    it.code() in 400..500 -> throw Exception("Unauthorized")
                    else -> throw Exception()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _cartLiveData.value = AddToCartState.ProcessDone }
            .subscribe({
                _cartLiveData.value = AddToCartState.CartSaved
            }, {
                val exception =
                    when (it) {
                        is SocketTimeoutException -> Exception("Socket timeout exception")
                        is IOException -> Exception("No Internet connection")
                        else -> {
                            Exception(it?.message ?: "Something went wrong")
                        }
                    }
                _cartLiveData.value = AddToCartState.Error(exception)
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

sealed class AddToCartState {
    object Loading : AddToCartState()
    object ProcessDone : AddToCartState()
    object CartSaved : AddToCartState()
    data class Error(val throwable: Throwable) : AddToCartState()
}
enum class ProductType {
    SHOP,
    SEARCH
}