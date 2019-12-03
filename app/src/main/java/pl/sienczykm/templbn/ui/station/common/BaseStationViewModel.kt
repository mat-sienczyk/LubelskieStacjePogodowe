package pl.sienczykm.templbn.ui.station.common

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.BaseStationModel
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

    abstract suspend fun updateFavourite(station: T): Int

    fun handleFavorite() {
        viewModelScope.launch {
            val station = station.value ?: throw Exception("Station is null")
            if (updateFavourite(station) > 0) {
                if (!station.favorite) getNavigator()?.showInfo(R.string.added_to_favorites)
                else getNavigator()?.showInfo(R.string.removed_from_favorites)
            }
        }
    }

    override fun refresh() {
        if (stationId == 0) throw Exception("Invalid stationId")
    }
}