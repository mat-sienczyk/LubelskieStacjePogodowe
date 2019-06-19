package pl.sienczykm.templbn.ui.station

import pl.sienczykm.templbn.ui.common.BaseNavigator

interface StationNavigator : BaseNavigator {
    fun openCustomTab(url: String?)
}