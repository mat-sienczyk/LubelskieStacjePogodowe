package pl.sienczykm.templbn.ui.list.weather

import android.app.Application
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.list.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler
import pl.sienczykm.templbn.utils.isAutoUpdateEnabled

class WeatherViewModel(application: Application) : BaseStationListViewModel<WeatherStationModel>(application) {

    override fun refresh() {
        UpdateHandler.syncNowWeatherStations(getApplication(), receiver)
    }

    override val stationsLiveData =
        AppDb.getDatabase(getApplication()).weatherStationDao().getAllStationsLiveData()

    init {
        if (!application.isAutoUpdateEnabled()) refresh()

        stations.addSource(stationsLiveData) { result: List<WeatherStationModel>? ->
            result?.let {
                stations.value = sortStations(it)
            }
        }
    }
}