package com.app.easyday.screens.base

import androidx.lifecycle.ViewModel
import com.app.easyday.navigation.SingleLiveEvent
import com.app.easyday.navigation.UiEvent

open class BaseViewModel : ViewModel() {

    val uiEventStream: SingleLiveEvent<UiEvent> = SingleLiveEvent()

    open fun onActivityCreated() {

    }

    open fun onFragmentCreated() {

    }

}