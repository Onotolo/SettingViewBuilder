package my.onotolo.svb

import my.onotolo.svb.settings.VisibleSetting

@SettingsBuilderDomainClass
open class SettingConfiguration<T>(val setting: VisibleSetting<T>) {
    var onSettingChangeCallback: OnSettingChangeCallback<T> = { _, _ -> }

    fun withCallback(callback: OnSettingChangeCallback<T>) {
        onSettingChangeCallback = callback
    }
}