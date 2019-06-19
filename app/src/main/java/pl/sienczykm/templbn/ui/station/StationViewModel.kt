package pl.sienczykm.templbn.ui.station

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.sienczykm.templbn.background.ProcessingUtils
import pl.sienczykm.templbn.background.StatusReceiver
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.StationModel
import pl.sienczykm.templbn.utils.UpdateHandler
import java.lang.ref.WeakReference

class StationViewModel(application: Application) : AndroidViewModel(application) {

    val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
    lateinit var station: LiveData<StationModel>
    private lateinit var navigator: WeakReference<StationNavigator>

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

    fun openCustomTab() {
        getNavigator()?.openCustomTab(station.value?.url)
    }

    fun refresh(type: StationFragment.Type, stationId: Int) {
        if (stationId == 0) throw Exception("Invalid stationId")
        else when (type) {
            StationFragment.Type.WEATHER -> {
                station =
                    AppDb.getDatabase(getApplication()).weatherStationDao().getStationById(stationId) as LiveData<StationModel>

                UpdateHandler.syncNowWeatherStation(getApplication(), stationId, receiver)
            }
            StationFragment.Type.SMOG -> {
                station =
                    AppDb.getDatabase(getApplication()).smogStationDao().getStationById(stationId) as LiveData<StationModel>

                UpdateHandler.syncNowSmogStation(getApplication(), stationId, receiver)

            }
        }
    }

    fun onRunning() {
        isRefreshing.value = true
    }

    fun onIdle() {
        isRefreshing.value = false
    }

    fun onError(resultData: String?) {
        isRefreshing.value = false
        getNavigator()?.handleError(resultData)
    }

    fun onNoConnection() {
        isRefreshing.value = false
        getNavigator()?.noConnection()
    }

    fun getNavigator(): StationNavigator? {
        return navigator.get()
    }

    fun setNavigator(navigator: StationNavigator) {
        this.navigator = WeakReference(navigator)
    }

}