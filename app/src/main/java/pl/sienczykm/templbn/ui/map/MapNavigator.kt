package pl.sienczykm.templbn.ui.map

import pl.sienczykm.templbn.ui.common.Navigator

interface MapNavigator : Navigator {
    fun openFilters()
    fun updateMap()
}