package com.buymee.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    lateinit var selectedProductId: String
    var isDeepLink: Boolean = false
    private val _loadingLiveData = MutableLiveData<ProgressLongBackgroundProcess>()
    val loadingLiveData: LiveData<ProgressLongBackgroundProcess>
        get() = _loadingLiveData

    private val _sessionLiveData = MutableLiveData<SessionState>()
    val sessionLiveData: LiveData<SessionState>
        get() = _sessionLiveData

    private val _toolbarLiveData = MutableLiveData<ToolbarElementsVisibility>()
    val toolbarLiveData: LiveData<ToolbarElementsVisibility>
        get() = _toolbarLiveData

    fun loading() {
        _loadingLiveData.value = ProgressLongBackgroundProcess.Loading
    }

    fun processDone() {
        _loadingLiveData.value = ProgressLongBackgroundProcess.ProcessDone
    }

    fun setSession(isSignedIn: Boolean){
        _sessionLiveData.value = SessionState(isSignedIn = isSignedIn)
    }

    fun toolBarElementsVisibility(
        isBackButtonVisible: Boolean,
        isShareButtonVisible: Boolean,
        toolbarTitleText: String = "BuyMe"
    ) {
        _toolbarLiveData.value =
            ToolbarElementsVisibility(isBackButtonVisible, isShareButtonVisible, toolbarTitleText)
    }
}

sealed class ProgressLongBackgroundProcess {
    object Loading : ProgressLongBackgroundProcess()
    object ProcessDone : ProgressLongBackgroundProcess()
}

data class ToolbarElementsVisibility(
    val isBackButtonVisible: Boolean,
    val isShareButtonVisible: Boolean,
    val toolbarTitleText: String
)

data class SessionState(
    val isSignedIn: Boolean
)
