package my.onotolo.svb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.onotolo.andrset.Settings
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

abstract class SettingViewBuilder<T: Any>(protected val setting: Settings<T>) {

    protected abstract val viewResources: WeakHashMap<Class<*>, Int>

    protected var onSettingChangeCallback: (T) -> Unit = {}

    infix fun withOnSettingChangeCallback(callback: (T) -> Unit): SettingViewBuilder<T> {
        onSettingChangeCallback = callback
        return this
    }

    fun build(parent: ViewGroup, attachToParent: Boolean = true): View {
        return inflateView(parent, attachToParent)
    }

    protected open fun inflateView(parent: ViewGroup, attachToParent: Boolean): View {
        var clazz: Class<*> =
                setting.defaultValue::class.javaPrimitiveType
                        ?: setting.defaultValue::class.java

        var resource: Int?
        do {
            resource = viewResources[clazz]
            clazz = clazz.superclass ?: break
        } while (resource == null && clazz != JvmType.Object::class.java)

        if (resource == null)
            throw Exception("Layout resource not defined for class ${
                setting.defaultValue::class.java.canonicalName}")

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)

        prepareView(view, setting[parent.context], resource)
        if (attachToParent)
            parent.addView(view)

        return view
    }

    abstract fun prepareView(view: View, value: T?, layoutRes: Int)
}