package pl.sienczykm.templbn.ui.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.lang.ref.WeakReference

abstract class NavigatorAndroidViewModel<T : Navigator>(application: Application) :
    AndroidViewModel(application) {

    private lateinit var navigator: WeakReference<T>

    fun getNavigator(): T? {
        return navigator.get()
    }

    fun setNavigator(navigator: T) {
        this.navigator = WeakReference(navigator)
    }

}