package pl.sienczykm.templbn.ui.common

import pl.sienczykm.templbn.db.model.ChartDataModel

interface StationNavigator : BaseNavigator {
    fun openCustomTab(url: String?)
    fun showChart(chartData: List<ChartDataModel>)
}