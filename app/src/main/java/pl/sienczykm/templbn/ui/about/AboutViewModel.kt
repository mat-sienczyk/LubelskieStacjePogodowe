package pl.sienczykm.templbn.ui.about

import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

class AboutViewModel : ViewModel() {

    private lateinit var navigator: WeakReference<AboutNavigator>

    fun openGooglePlay(appId: String) {
        getNavigator()?.openGooglePlay(appId)
    }

    fun openDialog(dialogType: AboutFragment.DialogType) {
        getNavigator()?.openDialog(dialogType)
    }

    fun openUrl(url: String) {
        getNavigator()?.openUrl(url)
    }

    private fun getNavigator(): AboutNavigator? {
        return navigator.get()
    }

    fun setNavigator(navigator: AboutNavigator) {
        this.navigator = WeakReference(navigator)
    }

}