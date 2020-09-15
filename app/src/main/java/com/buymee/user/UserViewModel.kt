package com.buymee.user

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buymee.data.UserPrefs
import com.buymee.network.ApiService
import com.buymee.network.LoginRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.SocketTimeoutException

class UserViewModel constructor(
    private val apiService: ApiService,
    private val context: Context
) : ViewModel() {

    private var compositeDisposable = CompositeDisposable()
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

    fun logout(){
        UserPrefs.getInstance(context).logout()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

sealed class LoginState {
    object SuccessfulLogin : LoginState()
    data class Error(val throwable: Throwable) : LoginState()
    object Loading : LoginState()
    object ProcessDone : LoginState()
}