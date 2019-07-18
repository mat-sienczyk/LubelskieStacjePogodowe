package pl.sienczykm.templbn.ui.common

import android.app.Application
import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import pl.sienczykm.templbn.background.ProcessingUtils
import pl.sienczykm.templbn.background.StatusReceiver
import java.lang.ref.WeakReference

abstract class BaseRefreshViewModel<N : BaseNavigator>(application: Application) : AndroidViewModel(application) {

    // changed to ObservableBoolean from LiveData because of bug https://stackoverflow.com/questions/57051586/problem-with-databinding-and-mutablelivedata
    // also it's not a big deal according to https://medium.com/androiddevelopers/android-data-binding-library-from-observable-fields-to-livedata-in-two-steps-690a384218f2
    val isRefreshing = ObservableBoolean(false)
    private lateinit var navigator: WeakReference<N>

    val receiver = object : StatusReceiver.Receiver {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            when (resultCode) {
                StatusReceiver.STATUS_RUNNING -> onRunning()
                StatusReceiver.STATUS_IDLE -> onIdle()
                StatusReceiver.STATUS_NO_CONNECTION -> onNoConnection()
                StatusReceiver.STATUS_ERROR -> onError(resultData.getString(ProcessingUtils.ERROR_KEY))
            }
        }
    }

    abstract fun refresh()

    fun onRunning() {
        isRefreshing.set(true)
    }

    fun onIdle() {
        isRefreshing.set(false)
    }

    fun onError(resultData: String?) {
        isRefreshing.set(false)
        getNavigator()?.handleError(resultData)
    }

    fun onNoConnection() {
        isRefreshing.set(false)
        getNavigator()?.noConnection()
    }

    fun getNavigator(): N? {
        return navigator.get()
    }

    fun setNavigator(navigator: N) {
        this.navigator = WeakReference(navigator)
    }
}