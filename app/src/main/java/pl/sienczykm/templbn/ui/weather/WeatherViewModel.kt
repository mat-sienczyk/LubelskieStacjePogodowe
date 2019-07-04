package pl.sienczykm.templbn.ui.weather

import android.app.Application
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherViewModel(application: Application) : BaseStationListViewModel<WeatherStationModel>(application) {

    init {
        refresh()
    }

    override fun refresh() {
        UpdateHandler.syncNowWeatherStations(getApplication(), receiver)
    }

    override val stationsLiveData =
        AppDb.getDatabase(getApplication()).weatherStationDao().getAllStationsLiveData()

    init {
        stations.addSource(stationsLiveData) { result: List<WeatherStationModel>? ->
            result?.let {
                stations.value = sortStations(it)
            }
        }
    }
}