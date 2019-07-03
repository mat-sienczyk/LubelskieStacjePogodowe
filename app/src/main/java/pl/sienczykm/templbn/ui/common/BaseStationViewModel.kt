package pl.sienczykm.templbn.ui.common

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel

abstract class BaseStationViewModel<T : BaseStationModel>(
    application: Application,
    private val stationId: Int = 0
) :
    BaseRefreshViewModel<StationNavigator>(application) {

    abstract val station: LiveData<T>

    fun openCustomTab() {
        getNavigator()?.openCustomTab(station.value?.url)
    }

    fun showChart(chartData: List<ChartDataModel>?) {
        if (chartData != null) getNavigator()?.showChart(chartData)
    }

    override fun refresh() {
        if (stationId == 0) throw Exception("Invalid stationId")
    }
}