package com.festum.festumfield.verstion.firstmodule.navigation

import androidx.annotation.StringRes

sealed class UiEvent {

    class ShowToast(@StringRes val messageResId: Int) : UiEvent()

    class ShowToastMsg(val message: String) : UiEvent()

    object HideKeyboard : UiEvent()

}