package pl.sienczykm.templbn.ui.air

import android.app.Application
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class AirViewModel(application: Application) : BaseStationListViewModel<AirStationModel>(application) {

    init {
        refresh()
    }

    override fun refresh() {
        UpdateHandler.syncNowSmogStations(getApplication(), receiver)
    }

    override val stationsLiveData =
        AppDb.getDatabase(getApplication()).airStationDao().getAllStationsLiveData()

    init {
        stations.addSource(stationsLiveData) { result: List<AirStationModel>? ->
            result?.let {
                stations.value = sortStations(it)
            }
        }
    }
}