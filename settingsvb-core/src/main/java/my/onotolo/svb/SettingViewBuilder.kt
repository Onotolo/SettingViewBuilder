package my.onotolo.svb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.onotolo.andrset.Setting
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

typealias CancelAction = () -> Unit
typealias OnSettingChangeCallback<T> = (T, CancelAction) -> Unit
typealias BindFunction<T> = (view: View, value: T, setting: Setting<T>, callback: OnSettingChangeCallback<T>) -> Unit

abstract class SettingViewBuilder<T: Any>(protected val setting: Setting<T>) {

    protected abstract val viewResources: WeakHashMap<Class<*>, Int>

    protected abstract val bindFunctions: ClassFuncMap

    protected var onSettingChangeCallback: (T, CancelAction) -> Unit = { _, _ -> }

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

        prepareView(view)
        if (attachToParent)
            parent.addView(view)

        return view
    }

    protected fun prepareView(view: View) {

        var clazz: Class<*> =
            setting.defaultValue::class.javaPrimitiveType
                ?: setting.defaultValue.javaClass
                ?: throw error("Can't find java class for type ${
                setting.defaultValue::class.simpleName}")

        var bindFunction: BindFunction<T>?
        do {
            bindFunction = bindFunctions[clazz] as? BindFunction<T>
            clazz = clazz.superclass ?: break
        } while (bindFunction == null && clazz != JvmType.Object::class.java)

        if (bindFunction == null)
            throw Exception("Bind function resource not defined for class ${
            setting.defaultValue::class.java.canonicalName}")

        bindFunction(view, setting[view.context], setting, onSettingChangeCallback)
    }
}