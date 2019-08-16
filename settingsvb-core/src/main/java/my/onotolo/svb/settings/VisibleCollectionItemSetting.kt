package my.onotolo.svb.settings

import android.content.Context

abstract class VisibleCollectionItemSetting<T> : VisibleSetting<T>() {

    abstract val collection: Collection<T>

    override fun get(context: Context?): T {
        val index =
            settingsProvider.getValue(
                context,
                id,
                collection.indexOf(defaultValue)
            )
        return collection.elementAt(index)
    }

    override fun set(context: Context?, value: T) {
        settingsProvider.setValue(
            context,
            id,
            collection.indexOf(value)
        )
    }
}