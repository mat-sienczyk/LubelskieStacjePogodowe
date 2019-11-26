package pl.sienczykm.templbn.ui.common

import androidx.annotation.StringRes
import pl.sienczykm.templbn.db.model.ChartDataModel

interface StationNavigator : BaseNavigator {
    fun openCustomTab(url: String)
    fun showChart(
        chartData: List<ChartDataModel>,
        minIsZero: Boolean,
        limitValue: Float?,
        unit: String,
        title: String? = null,
        @StringRes titleId: Int? = null
    )
}