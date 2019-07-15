# SettingViewBuilder
This library is used to create views that represent settings created using [`AndroidSettings`](https://github.com/Onotolo/AndroidSettings) library in your Android application.
## Setup
Project's `build.gradle`:
```groovy
allprojects {
    repositories {
        ...
        maven {
            url = "https://raw.githubusercontent.com/Onotolo/AndroidLibsMavenRepo/master"
        }
    }
}
```

Module's `build.gradle`:
```groovy
dependencies {
    ...
    implementation 'my.onotolo.android:svb:0.0.7'
    implementation 'my.onotolo.android:android-settings:0.0.2'
}
```

## Preparation
### Make some `Setting`
First, you need a setting which you want to create your view for, e.g.:
```kotlin
object IsTimerHidden: BaseSetting<Boolean>() {

    override val defaultValue = false

    override val settingNameResId = R.string.set_hide_timer
    override val descriptionResId = R.string.set_hide_timer_descr
    override val id: String = "Hide timer"
}
```
> For more information about this step refer to [AndroidSettings](https://github.com/Onotolo/AndroidSettings) library
### Create layout file
Your next step is to create layout file for this type of setting.
As an example, for my `Boolean` setting I'll create a layout containing two `TextView`s for name and description and `Switch` to represent condition of the setting:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingTop="16dp"
    android:paddingBottom="16dp">

    <TextView
        android:id="@+id/settings_line_name"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:paddingEnd="8dp"
        android:text="Setting name"
        android:textSize="18sp"
        app:layout_constraintEnd_toStartOf="@+id/settings_line_switch"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/settings_line_descr"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="2dp"
        android:layout_marginEnd="16dp"
        android:text="Setting description"
        android:textAlignment="viewStart"
        app:layout_constraintEnd_toStartOf="@+id/settings_line_switch"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/settings_line_name" />

    <Switch
        android:id="@+id/settings_line_switch"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
My layout will look somehow like this:
![Image of settings line preview](https://onotolo.github.io/SettingViewBuilder/images/bool_settings_line.png)

### Implementing `SettingViewBuilder`
Now it's time to implement `SettingViewBuilder`:
```kotlin
typealias CancelAction = () -> Unit
typealias OnSettingChangeCallback<T> = (T, CancelAction) -> Unit

class SettingViewBuilderImpl<T : Any> constructor(setting: Setting<T>):
        SettingViewBuilder<T>(setting) {

    override val viewResources = WeakHashMap(mapOf(
            Boolean::class.java to R.layout.settings_line_boolean
    ))

    companion object {
        infix fun <T: Any>forSetting(setting: Setting<T>): SettingViewBuilderImpl<T> {
            return SettingViewBuilderImpl(setting)
        }
    }

    override fun prepareView(view: View, value: T?, layoutRes: Int, , callback: OnSettingChangeCallback<T>) {
        when (setting.defaultValue) {
            is Boolean -> prepareBoolean(view, setting as Setting<Boolean>, callback as OnSettingChangeCallback<Boolean>)
            else -> throw Exception("Type needs array of values provided")
        }
    }

    private fun prepareBoolean(view: View, setting: Setting<Boolean>, callback: OnSettingChangeCallback<Boolean>) {
    
        view.settings_line_name.text = setting.getName(view.context)

        if (setting.getDescription(view.context) != null) {
            view.settings_line_descr?.text = setting.getDescription(view.context)
        } else
            view.settings_line_descr?.visibility = View.GONE

        val switch = view.settings_line_switch

        switch.isChecked = setting[view.context]

        view.setOnClickListener {

            val value = !setting[view.context]
            setting[view.context] = value

            switch.isChecked = value

            callback(value) {
                view.callOnClick()
            }
        }
        switch.setOnCheckedChangeListener { buttonView, isChecked ->

            val value = setting[view.context]
            if (value == isChecked)
                return@setOnCheckedChangeListener
                
            setting[buttonView.context] = isChecked
            callback(value) {
                switch.callOnClick()
            }
        }
    }
}
```
As you can see, when implementing view builer one must implement `viewResources` field and `prepareView` method:
* `viewResources` field is used to bind settings types to layout resource files. You must specify layout resource for each type of settings you build views for, otherwise exception will be thrown.
* `prepareView` method is used to configure a view created by builder. This is a place to bind setting to it's view, e.g. set name and description, put listeners and so on.
## Usage
Once you've prepared all the stuff mentioned before you are able to add a view for your setting in any container view you can reach through code:
```kotlin
{
    ...
    (SettingViewBuilderImpl forSetting IsTimerHidden)
        .withOnSettingChangeCallback { value, cancel -> 
            ...
        }
        .build(containerView)
    ...
}
