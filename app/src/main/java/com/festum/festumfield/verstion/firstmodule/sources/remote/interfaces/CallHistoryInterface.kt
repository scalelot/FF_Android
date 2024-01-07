package com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces

import com.festum.festumfield.verstion.firstmodule.sources.remote.model.CallHistoryItem
import java.io.Serializable

interface CallHistoryInterface {

    fun onCallFromHistory(item: CallHistoryItem?)

}