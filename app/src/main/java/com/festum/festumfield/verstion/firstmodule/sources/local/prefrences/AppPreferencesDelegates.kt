package com.festum.festumfield.verstion.firstmodule.sources.local.prefrences


class AppPreferencesDelegates private constructor() {

    var token by TokenDelegate()
    var channelId by ChannelIdDelegate()
    var userName by UserNameDelegate()
    var personalProfile by PersonalProfileRegisteredDelegate()
    var businessProfile by BusinessProfileRegisteredDelegate()
    var onLineUser by OnlineIdDelegate()
    var notificationToken by NotificationTokenDelegate()

    companion object {
        private var INSTANCE: AppPreferencesDelegates? = null
        fun get(): AppPreferencesDelegates = INSTANCE ?: AppPreferencesDelegates()
    }

}