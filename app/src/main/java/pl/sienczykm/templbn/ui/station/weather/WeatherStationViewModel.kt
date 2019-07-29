package pl.sienczykm.templbn.ui.station.weather

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.station.common.BaseStationViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherStationViewModelFactory(
    private val application: Application,
    private val stationId: Int = 0
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherStationViewModel(
            application,
            stationId
        ) as T
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

    fun showChart(
        chartData: List<ChartDataModel>?,
        minIsZero: Boolean,
        unit: String,
        title: String
    ) {
        chartData?.let { getNavigator()?.showChart(it, minIsZero, null, unit, title) }
    }
}