package pl.sienczykm.templbn.ui.common

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import pl.sienczykm.templbn.background.ProcessingUtils
import pl.sienczykm.templbn.background.StatusReceiver

abstract class BaseRefreshViewModel<N : BaseRefreshNavigator>(application: Application) :
    NavigatorAndroidViewModel<N>(application) {

    val isRefreshing = MutableLiveData<Boolean>().apply { value = false }

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
}