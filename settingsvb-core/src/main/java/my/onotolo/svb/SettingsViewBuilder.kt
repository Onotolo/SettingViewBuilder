package my.onotolo.svb

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import my.onotolo.svb.settings.VisibleSetting
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@DslMarker
annotation class SettingsBuilderDomainClass

inline fun <reified TBuilder : SettingsViewBuilder>
        markupSettingsView(
    parent: ViewGroup,
    adapter: SettingsAdapter,
    init: TBuilder.() -> Unit
): TBuilder {

    val builder = createBuilder(TBuilder::class, parent, adapter)
    builder.init()

    return builder
}

fun <TBuilder : SettingsViewBuilder> createBuilder(
    clazz: KClass<TBuilder>,
    parent: ViewGroup, adapter: SettingsAdapter
): TBuilder {
    return try {
        clazz.primaryConstructor?.call(parent, adapter)
    } catch (e: Exception) {
        null
    } ?: throw Exception(
        "Could not call primary constructor of class ${clazz.simpleName} with given parameters" +
                "${parent::class.simpleName}, ${adapter::class.simpleName}.\n" +
                "To instantiate object of this class one needs to provide following parameters: " +
                "${clazz.primaryConstructor?.parameters
                    ?.map { (it.type.classifier as KClass<*>).simpleName }
                    ?.joinToString(" and ")}.")
}

fun <TBuilder : SettingsViewBuilder>
        TBuilder.section(header: String, init: TBuilder.() -> Unit): ViewGroup {

    val sectionView = adapter.addSectionToParent(parent)
    val builder = createBuilder(
        this::class,
        sectionView,
        adapter
    )
    adapter.addHeaderToSection(sectionView).text = header

    builder.init()

    return sectionView
}

@SettingsBuilderDomainClass
open class SettingsViewBuilder
    (val parent: ViewGroup, val adapter: SettingsAdapter) {

    fun <T : Any>
            setting(setting: VisibleSetting<T>, init: SettingConfiguration<T>.() -> Unit) =
        settingBase(setting, init)

    fun <T : Any>
            setting(setting: VisibleSetting<T>): View = setting(setting) {}

    /**
     * This fun provides a way to write DSL-methods with specified subtype of [SettingConfiguration]
     * @see [SettingsViewBuilder.setting] as example
     */
    protected inline fun <T : Any, reified TConfig : SettingConfiguration<T>>
            settingBase(setting: VisibleSetting<T>, init: TConfig.() -> Unit): View {
        val configuration = createConfig<T, TConfig>(setting)
        configuration.init()
        val builder = SettingViewBuilder(configuration, setting, adapter)
        return builder.build(parent)
    }

    protected inline fun <T : Any, reified TConfig : SettingConfiguration<T>>
            createConfig(setting: VisibleSetting<T>): TConfig = try {
        TConfig::class.primaryConstructor?.call(setting)
    } catch (e: Exception) {
        null
    } ?: throw IllegalArgumentException(
        "${TConfig::class.simpleName} class should have " +
                "a constructor with single [VisibleSetting<T>] parameter"
    )
}

/**
 * This interface describes properties and functions that should be implemented
 *   for [SettingsViewBuilder] to work in your environment
 *
 *   @see [SettingsViewBuilder.adapter]
 */
interface SettingsAdapter {

    val bindFunctions: ClassFuncMap
    val viewResources: Map<Class<*>, Int>

    /**
     * Inside this function [ViewGroup] which will represent a section of your settings
     *   should be created and added to the parent [ViewGroup].
     *
     * This function is also responsible for placing created section within it's parent.
     *
     * @see my.onotolo.svb.section
     *
     * @return created section ([ViewGroup])
     */
    fun addSectionToParent(parent: ViewGroup): ViewGroup

    /**
     * Inside this function [TextView] which will be a header of [section]
     *   should be created and placed within it.
     *
     * @see my.onotolo.svb.section
     *
     * @return created header ([TextView])
     */
    fun addHeaderToSection(section: ViewGroup): TextView
}