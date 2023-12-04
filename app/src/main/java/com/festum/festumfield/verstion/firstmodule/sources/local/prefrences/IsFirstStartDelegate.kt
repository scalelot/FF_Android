package com.festum.festumfield.verstion.firstmodule.sources.local.prefrences

import com.pixplicity.easyprefs.library.Prefs
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class IsFirstStartDelegate : ReadWriteProperty<AppPreferencesDelegates, Boolean> {

    companion object {
        const val PREF_KEY_TOKEN = "IsFirstStartDelegate"
    }

    override fun getValue(thisRef: AppPreferencesDelegates, property: KProperty<*>): Boolean =
        Prefs.getBoolean(PREF_KEY_TOKEN, false)

    override fun setValue(
        thisRef: AppPreferencesDelegates,
        property: KProperty<*>,
        value: Boolean
    ) {
        Prefs.putBoolean(PREF_KEY_TOKEN, value)
    }
}
