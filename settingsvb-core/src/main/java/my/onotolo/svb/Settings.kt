package my.onotolo.svb

import android.content.Context

abstract class Settings<T> {

    abstract val settingNameResId: Int
    abstract val descriptionResId: Int?

    fun getName(context: Context?): String? {
        return context?.getString(settingNameResId)
    }
    fun getDescription(context: Context?): String? {
        val resId = descriptionResId ?: return null
        return context?.getString(resId)
    }

    abstract val id: String

    abstract val defaultValue: T

    open operator fun get(context: Context?): T {
        return SettingsProvider.getValue(context, id, defaultValue)
    }

    open operator fun set(context: Context?, value: T) {
        SettingsProvider.setValue(context, id, value)
    }
}