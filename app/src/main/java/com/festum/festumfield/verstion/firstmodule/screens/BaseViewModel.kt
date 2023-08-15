package com.festum.festumfield.verstion.firstmodule.screens

import androidx.lifecycle.ViewModel
import com.festum.festumfield.verstion.firstmodule.navigation.SingleLiveEvent
import com.festum.festumfield.verstion.firstmodule.navigation.UiEvent

open class BaseViewModel : ViewModel() {

    val uiEventStream: SingleLiveEvent<UiEvent> = SingleLiveEvent()

    open fun onActivityCreated() {

    }

    open fun onFragmentCreated() {

    }

}