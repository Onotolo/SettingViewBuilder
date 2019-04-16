package my.onotolo.svb

import android.content.Context

abstract class Settings<T> {

    abstract val settingName: String
    abstract val description: String?
    abstract val id: String

    abstract val defaultValue: T

    open operator fun get(context: Context?): T {
        return SettingsProvider.getValue(context, id, defaultValue)
    }

    open operator fun set(context: Context?, value: T) {
        SettingsProvider.setValue(context, id, value)
    }
}