package pl.sienczykm.templbn.ui.common

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.sienczykm.templbn.background.UpdateReceiver

abstract class BaseStationListViewModel<T>(application: Application) : AndroidViewModel(application) {


    val isRefreshing = MutableLiveData<Boolean>()

    init {
        isRefreshing.value = false
    }

    val receiver = object : UpdateReceiver.Receiver {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            when (resultCode) {
                UpdateReceiver.STATUS_RUNNING -> isRefreshing.value = true
                UpdateReceiver.STATUS_FINISHED -> isRefreshing.value = false
                UpdateReceiver.STATUS_NO_CONNECTION -> onNoConnection()
                UpdateReceiver.STATUS_ERROR -> onError(resultData)
            }
        }
    }

    abstract val stations: LiveData<List<T>>

    abstract fun refresh()

    fun getAllStations(): LiveData<List<T>> {
        return stations
    }

    fun onError(resultData: Bundle) {
        isRefreshing.value = false
    }

    private fun onNoConnection() {
        isRefreshing.value = false
    }
}