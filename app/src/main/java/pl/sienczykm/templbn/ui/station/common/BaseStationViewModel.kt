package pl.sienczykm.templbn.ui.station.common

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.ui.common.BaseRefreshViewModel
import pl.sienczykm.templbn.ui.common.StationNavigator

abstract class BaseStationViewModel<T : BaseStationModel>(
    application: Application,
    private val stationId: Int = 0
) :
    BaseRefreshViewModel<StationNavigator>(application) {

    abstract val station: LiveData<T>

    fun openCustomTab(url: String?) {
        url?.let { getNavigator()?.openCustomTab(it) }
    }

    fun showChart(chartData: List<ChartDataModel>?) {
        chartData?.let { getNavigator()?.showChart(it) }
    }

    override fun refresh() {
        if (stationId == 0) throw Exception("Invalid stationId")
    }
}