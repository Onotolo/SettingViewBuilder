package my.onotolo.svb

import android.content.Context
import my.onotolo.andrset.Setting

abstract class VisibleSetting<T> : Setting<T>() {

    abstract val settingNameResId: Int
    abstract val descriptionResId: Int?

    fun getName(context: Context?): String? {
        return context?.getString(settingNameResId)
    }

    fun getDescription(context: Context?): String? {
        val resId = descriptionResId ?: return null
        return context?.getString(resId)
    }

}