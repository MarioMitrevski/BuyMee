package com.buymee.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _loadingLiveData = MutableLiveData<ProgressLongBackgroundProcess>()
    val loadingLiveData: LiveData<ProgressLongBackgroundProcess>
        get() = _loadingLiveData

    private val _toolbarLiveData = MutableLiveData<ToolbarElementsVisibility>()
    val toolbarLiveData: LiveData<ToolbarElementsVisibility>
        get() = _toolbarLiveData

    fun loading() {
        _loadingLiveData.value = ProgressLongBackgroundProcess.Loading
    }

    fun processDone() {
        _loadingLiveData.value = ProgressLongBackgroundProcess.ProcessDone
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