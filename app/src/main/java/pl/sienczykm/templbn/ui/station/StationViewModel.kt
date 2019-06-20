package pl.sienczykm.templbn.ui.station

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.StationModel
import pl.sienczykm.templbn.ui.common.BaseRefreshViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class StationViewModel(
    application: Application,
    private val type: StationFragment.Type,
    private val stationId: Int = 0
) :
    BaseRefreshViewModel<StationNavigator>(application) {

    init {
        refresh()
    }

    lateinit var station: LiveData<StationModel>

    fun openCustomTab() {
        getNavigator()?.openCustomTab(station.value?.url)
    }

    override fun refresh() {
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
}