package pl.sienczykm.templbn.ui.list.air

import android.app.Application
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.ui.list.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class AirStationListViewModel(application: Application) :
    BaseStationListViewModel<AirStationModel>(application) {

    override fun refresh() {
        UpdateHandler.syncNowAirStations(getApplication(), receiver)
    }

    override val stationsLiveData =
        AppDb.getDatabase(getApplication()).airStationDao().getAllStationsLiveData()

    init {
        refresh()

        stations.addSource(stationsLiveData) { result: List<AirStationModel>? ->
            result?.let {
                stations.value = sortStations(it)
            }
        }
    }

    override suspend fun updateFavourite(station: BaseStationModel): Int =
        AppDb.getDatabase(getApplication()).airStationDao().updateFavoriteSuspend(
            station.stationId, !station.favorite
        )
}