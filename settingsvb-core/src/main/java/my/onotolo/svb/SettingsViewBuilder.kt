package my.onotolo.svb

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import my.onotolo.svb.settings.VisibleSetting
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@DslMarker
annotation class SettingsBuilderDomainClass

inline fun <reified TBuilder : SettingsViewBuilder> markupSettingsView(
    parent: ViewGroup,
    adapter: SettingsViewBuilder.Adapter,
    init: TBuilder.() -> Unit
): TBuilder {

    val builder = createBuilder(TBuilder::class, parent, adapter)
    builder.init()

    return builder
}

fun <TBuilder : SettingsViewBuilder> createBuilder(
    clazz: KClass<TBuilder>,
    parent: ViewGroup, adapter: SettingsViewBuilder.Adapter
): TBuilder {
    return try {
        clazz.primaryConstructor?.call(parent, adapter)
    } catch (e: Exception) {
        null
    } ?: throw Exception(
        "Could not call primary constructor of class ${clazz.simpleName} with given parameters" +
                "${parent::class.simpleName}, ${adapter::class.simpleName}.\n" +
                "To instantiate this class one needs to provide following parameters: " +
                "${clazz.primaryConstructor?.parameters
                    ?.map { (it.type.classifier as KClass<*>).simpleName }
                    ?.joinToString(" and ")}.")
}

fun <TBuilder : SettingsViewBuilder> TBuilder.section(header: String, init: TBuilder.() -> Unit) {

    val sectionView = adapter.makeSectionView(parent)
    val builder = createBuilder(
        this::class,
        sectionView,
        adapter
    )
    adapter.makeSectionHeader(sectionView).text = header

    builder.init()
}

@SettingsBuilderDomainClass
open class SettingsViewBuilder(val parent: ViewGroup, val adapter: Adapter) {

    internal var creator: () -> SettingsViewBuilder = { createBuilder(this::class, parent, adapter) }

    fun <T : Any>
            setting(setting: VisibleSetting<T>, init: SettingConfiguration<T>.() -> Unit): View {
        val configuration = SettingConfiguration<T>()
        configuration.init()
        val builder = SettingViewBuilder(configuration, setting, adapter)
        return builder.build(parent)
    }

    fun <T : Any>
            setting(setting: VisibleSetting<T>): View {
        val configuration = SettingConfiguration<T>()
        val builder = SettingViewBuilder(configuration, setting, adapter)
        return builder.build(parent)
    }

    @SettingsBuilderDomainClass
    open class SettingConfiguration<T> {
        var onSettingChangeCallback: OnSettingChangeCallback<T> = { _, _ -> }

        fun withCallback(callback: OnSettingChangeCallback<T>) {
            onSettingChangeCallback = callback
        }
    }

    interface Adapter {
        val bindFunctions: ClassFuncMap
        val viewResources: Map<Class<*>, Int>

        fun makeSectionView(parent: ViewGroup): ViewGroup

        fun makeSectionHeader(section: ViewGroup): TextView
    }
}