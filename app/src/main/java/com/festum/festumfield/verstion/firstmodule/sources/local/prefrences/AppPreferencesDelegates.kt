package com.festum.festumfield.verstion.firstmodule.sources.local.prefrences


class AppPreferencesDelegates private constructor() {

    var token by TokenDelegate()
    var channelId by ChannelIdDelegate()

    companion object {
        private var INSTANCE: AppPreferencesDelegates? = null
        fun get(): AppPreferencesDelegates = INSTANCE ?: AppPreferencesDelegates()
    }

}