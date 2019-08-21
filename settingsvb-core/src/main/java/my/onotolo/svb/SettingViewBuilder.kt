package my.onotolo.svb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.onotolo.svb.settings.VisibleSetting
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

typealias CancelFunc = () -> Unit
typealias OnSettingChangeCallback<T> = (value: T, cancelFunc: CancelFunc) -> Unit
typealias BindFunction<T> = (view: View, configuration: SettingConfiguration<T>) -> Unit

class SettingViewBuilder<T : Any>(
    private val configuration: SettingConfiguration<T>,
    private val setting: VisibleSetting<T>,
    private val adapter: SettingsAdapter
) {

    fun build(parent: ViewGroup): View {
        return inflateView(parent)
    }

    private fun inflateView(parent: ViewGroup): View {
        var clazz: Class<*> =
                setting.defaultValue::class.javaPrimitiveType
                        ?: setting.defaultValue::class.java

        var resource: Int?
        do {
            resource = adapter.viewResources[clazz]
            clazz = clazz.superclass ?: break
        } while (resource == null && clazz != JvmType.Object::class.java)

        if (resource == null)
            throw Exception("Layout resource not defined for class ${
                setting.defaultValue::class.java.canonicalName}")

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)

        prepareView(view)
        parent.addView(view)

        return view
    }

    private fun prepareView(view: View) {

        var clazz: Class<*> =
            setting.defaultValue::class.javaPrimitiveType
                ?: setting.defaultValue.javaClass

        var bindFunction: BindFunction<T>?
        do {
            bindFunction = adapter.bindFunctions[clazz] as? BindFunction<T>
            clazz = clazz.superclass ?: break
        } while (bindFunction == null && clazz != JvmType.Object::class.java)

        if (bindFunction == null)
            throw Exception("Bind function resource not defined for class ${
            setting.defaultValue::class.java.canonicalName}")

        bindFunction(view, configuration)
    }
}