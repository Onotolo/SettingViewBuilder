package my.onotolo.svb

import android.app.Activity
import android.content.Context

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
object SettingsProvider {

    var PREFS_NAME = "Preferences"

    fun <T>setValue(context: Context?, settingId: String, value: T) {

        context?.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)?.edit()

            ?.apply{
                when (value) {
                    is Boolean -> putBoolean(settingId, value)
                    is Int -> putInt(settingId, value)
                    is String -> putString(settingId, value)
                }
            }?.apply()
    }

    fun <T>getValue(context: Context?, settingId: String, defaultValue: T): T {

        var value: T = defaultValue

        context?.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)?.apply {

            value = when (defaultValue) {

                is Boolean -> getBoolean(settingId, defaultValue)
                is Int -> getInt(settingId, defaultValue)
                is String -> getString(settingId, defaultValue)
                else -> defaultValue
            } as T
        }
        return value
    }
}