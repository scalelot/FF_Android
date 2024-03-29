package com.festum.festumfield.verstion.firstmodule.sources.local.prefrences


class AppPreferencesDelegates private constructor() {

    var token by TokenDelegate()
    var fcmToken by FcmTokenDelegate()
    var channelId by ChannelIdDelegate()
    var userName by UserNameDelegate()
    var personalProfile by PersonalProfileRegisteredDelegate()
    var businessProfile by BusinessProfileRegisteredDelegate()
    var onLineUser by OnlineIdDelegate()
    var notificationToken by NotificationTokenDelegate()
    var isVideoCalling by IsVideoCallDelegate()
    var isAudioCalling by IsAudioCallDelegate()
    var isNightModeOn by IsNightModeDelegate()
    var isFirstStart by IsFirstStartDelegate()
    var isCallId by CallIdDelegate()
    var isSocketId by SocketDelegate()
    var phoneNumber by PhoneNumberDelegate()
    var emailAddress by EmailAddressDelegate()
    var isNotificationOn by IsNotificationOnDelegate()
    var callAcceptIdDelegate by CallAcceptIdDelegate()

    companion object {
        private var INSTANCE: AppPreferencesDelegates? = null
        fun get(): AppPreferencesDelegates = INSTANCE ?: AppPreferencesDelegates()
    }

}