package my.onotolo.svb.navigation

import android.view.View
import android.view.ViewGroup
import my.onotolo.svb.SettingsViewBuilder

typealias BuildSettingsFunc = (parent: ViewGroup, adapter: NavigationSettingsViewBuilder.NavigationAdapter) -> Unit

class NavigationSettingsViewBuilder(parent: ViewGroup, adapter: NavigationAdapter) :
    SettingsViewBuilder(parent, adapter) {

    fun screen(title: String, init: NavigationSettingsViewBuilder.() -> Unit): View {

        if (adapter !is NavigationAdapter)
            throw IllegalStateException()

        return adapter.makeNavigationView(parent, title) { newParent, newAdapter ->
            val builder = NavigationSettingsViewBuilder(newParent, newAdapter)
            builder.init()
        }
    }

    abstract class NavigationAdapter : Adapter {

        /**
         * You should not set [View.onClickListener] here, it is done in [NavigationAdapter.makeNavigationView] function.
         * @return [View] onClick of which will trigger screen change
         */
        protected abstract fun makeView(parent: ViewGroup, title: String): View

        fun makeNavigationView(parent: ViewGroup, title: String, buildSettings: BuildSettingsFunc): View {
            val view = makeView(parent, title)

            view.setOnClickListener {
                navigateToScreen(title, buildSettings)
            }

            return view
        }

        /***
         *
         * @see [NavigationSettingsViewBuilder.screen]
         * @return ViewGroup - parent view group next builder will be applied to
         */
        protected abstract fun navigateToScreen(title: String, buildSettings: BuildSettingsFunc)
    }
}