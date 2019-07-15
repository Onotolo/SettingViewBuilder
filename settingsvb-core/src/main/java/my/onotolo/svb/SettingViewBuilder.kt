package my.onotolo.svb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.onotolo.andrset.Setting
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

typealias CancelAction = () -> Unit

abstract class SettingViewBuilder<T: Any>(protected val setting: Setting<T>) {

    protected abstract val viewResources: WeakHashMap<Class<*>, Int>

    protected var onSettingChangeCallback: (T, CancelAction) -> Unit = {_,_ -> }

    infix fun withOnSettingChangeCallback(callback: (T, CancelAction) -> Unit): SettingViewBuilder<T> {
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

        prepareView(view, setting[parent.context], resource, onSettingChangeCallback)
        if (attachToParent)
            parent.addView(view)

        return view
    }

    protected abstract fun prepareView(view: View, value: T?, layoutRes: Int, callback: (T, CancelAction) -> Unit)
}