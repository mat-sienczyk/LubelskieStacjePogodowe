package pl.sienczykm.templbn.ui.common

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import pl.sienczykm.templbn.background.ProcessingUtils
import pl.sienczykm.templbn.background.StatusReceiver
import java.lang.ref.WeakReference

abstract class BaseRefreshViewModel<N : BaseRefreshNavigator>(application: Application) :
    AndroidViewModel(application) {

    val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
    private lateinit var navigator: WeakReference<N>

    val receiver = object : StatusReceiver.Receiver {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            when (resultCode) {
                StatusReceiver.STATUS_REFRESHING -> onRefresh()
                StatusReceiver.STATUS_IDLE -> onIdle()
                StatusReceiver.STATUS_NO_CONNECTION -> onNoConnection()
                StatusReceiver.STATUS_ERROR -> onError(resultData.getStringArray(ProcessingUtils.ERROR_KEY))
            }
        }
    }

    abstract fun refresh()

    fun onRefresh() {
        isRefreshing.value = true
    }

    fun onIdle() {
        isRefreshing.value = false
    }

    fun onError(resultData: Array<String>?) {
        getNavigator()?.handleError(resultData)
    }

    fun onNoConnection() {
        getNavigator()?.noConnection()
    }

    fun getNavigator(): N? {
        return navigator.get()
    }

    fun setNavigator(navigator: N) {
        this.navigator = WeakReference(navigator)
    }
}