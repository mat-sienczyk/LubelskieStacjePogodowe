package pl.sienczykm.templbn.ui.station

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.BaseStationViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherStationViewModelFactory(
    private val application: Application,
    private val stationId: Int = 0
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherStationViewModel(application, stationId) as T
    }
}

class WeatherStationViewModel(
    application: Application,
    private val stationId: Int = 0
) :
    BaseStationViewModel<WeatherStationModel>(application, stationId) {

    override val station =
        AppDb.getDatabase(getApplication()).weatherStationDao().getStationLiveDataById(stationId)

    init {
        refresh()
    }

    override fun refresh() {
        super.refresh()
        UpdateHandler.syncNowWeatherStation(getApplication(), stationId, receiver)
    }
}