package pl.sienczykm.templbn.ui.common

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.model.StationModel

abstract class BaseStationViewModel<T : StationModel>(
    application: Application,
    private val stationId: Int = 0
) :
    BaseRefreshViewModel<StationNavigator>(application) {

    abstract val station: LiveData<T>

    fun openCustomTab() {
        getNavigator()?.openCustomTab(station.value?.url)
    }

    override fun refresh() {
        if (stationId == 0) throw Exception("Invalid stationId")
    }
}