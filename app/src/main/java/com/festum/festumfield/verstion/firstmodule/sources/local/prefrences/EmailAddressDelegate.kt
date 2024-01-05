package com.festum.festumfield.verstion.firstmodule.sources.local.prefrences

import com.pixplicity.easyprefs.library.Prefs
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class EmailAddressDelegate : ReadWriteProperty<AppPreferencesDelegates, String> {

    companion object {
        const val PREF_KEY_TOKEN = "emailAddress"
    }

    override fun getValue(thisRef: AppPreferencesDelegates, property: KProperty<*>): String =
        Prefs.getString(PREF_KEY_TOKEN, "")


    override fun setValue(
        thisRef: AppPreferencesDelegates,
        property: KProperty<*>,
        value: String
    ) {
        Prefs.putString(PREF_KEY_TOKEN, value)
    }
}
