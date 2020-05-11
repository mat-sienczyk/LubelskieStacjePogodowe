package pl.sienczykm.templbn.ui.list.weather

import android.app.Application
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.list.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherListViewModel(application: Application) :
    BaseStationListViewModel<WeatherStationModel>(application) {

    override fun refresh() {
        UpdateHandler.syncNowWeatherStations(getApplication(), receiver)
    }

    override val stationsLiveData =
        AppDb.getDatabase(getApplication()).weatherStationDao().getAllStationsLiveData()

    init {
        stations.addSource(stationsLiveData) { result: List<WeatherStationModel>? ->
            result?.let {
                stations.value = sortStations(it)
                if (!isRefreshedOnInit) refreshIfNeeded(it)
            }
        }
    }

    override fun refreshIfNeeded(stations: List<WeatherStationModel>) {
        //TODO this is working fine only when db is recreated from scratch
        stations.filter { it.isDateObsoleteOrNull() ?: true }
            .let { stationsToUpdate ->
                UpdateHandler.syncNowWeatherStations(
                    getApplication(),
                    receiver,
                    stationsToUpdate
                )
            }
        isRefreshedOnInit = true
    }

    override suspend fun updateFavourite(station: BaseStationModel): Int =
        AppDb.getDatabase(getApplication()).weatherStationDao().updateFavoriteSuspend(
            station.stationId, !station.favorite
        )
}