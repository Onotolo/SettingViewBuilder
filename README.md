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
    implementation 'my.onotolo.android:svb:0.0.5'
    implementation 'my.onotolo.android:android-settings:0.0.2'
}
```

## Usage

* First, you need a setting for which you want to create your view, e.g.:
```kotlin
object IsTimerHidden: BaseSetting<Boolean>() {

    override val defaultValue = false

    override val settingNameResId = R.string.set_hide_timer
    override val descriptionResId = R.string.set_hide_timer_descr
    override val id: String = "Hide timer"
}
```
For more information about this step refer to [AndroidSettings](https://github.com/Onotolo/AndroidSettings) library
* Your next step is to create layout file for this type of setting.
For our `Boolean` setting we'll create a layout containing two `TextView`s for name and description and `Switch` to represent condition of the setting:
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
As a result you should see something like this in your Layout Redactor's Preview:
