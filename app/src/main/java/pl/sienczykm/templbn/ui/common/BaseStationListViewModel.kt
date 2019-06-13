package pl.sienczykm.templbn.ui.common

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.sienczykm.templbn.background.ProcessingUtils
import pl.sienczykm.templbn.background.StatusReceiver

abstract class BaseStationListViewModel<T>(application: Application) : AndroidViewModel(application) {

    val isRefreshing = MutableLiveData<Boolean>()
    val status = MutableLiveData<Int>()

    init {
        isRefreshing.value = false
        status.value = StatusReceiver.STATUS_IDLE
    }

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

    abstract val stations: LiveData<List<T>>

    abstract fun refresh()

    fun getAllStations(): LiveData<List<T>> {
        return stations
    }

    fun onRunning() {
        isRefreshing.value = true
        status.value = StatusReceiver.STATUS_RUNNING
    }

    fun onIdle() {
        isRefreshing.value = false
        status.value = StatusReceiver.STATUS_IDLE
    }

    fun onError(resultData: String?) {
        isRefreshing.value = false
        status.value = StatusReceiver.STATUS_ERROR
        status.value = StatusReceiver.STATUS_IDLE
    }

    fun onNoConnection() {
        isRefreshing.value = false
        status.value = StatusReceiver.STATUS_NO_CONNECTION
        status.value = StatusReceiver.STATUS_IDLE
    }
}