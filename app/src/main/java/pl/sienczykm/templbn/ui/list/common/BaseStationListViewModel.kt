package pl.sienczykm.templbn.ui.list.common

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.ui.common.BaseNavigator
import pl.sienczykm.templbn.ui.common.BaseRefreshViewModel
import pl.sienczykm.templbn.utils.haversine

abstract class BaseStationListViewModel<T : BaseStationModel>(application: Application) :
    BaseRefreshViewModel<BaseNavigator>(application) {

    // change this BaseStationModel to T when databinding lib is fixed
    val stations = MediatorLiveData<List<BaseStationModel>>()

    abstract val stationsLiveData: LiveData<List<T>>

    var coordinates: Location? = null
        set(value) {
            field = value
            stationsLiveData.value?.let { stations.value = sortStations(it) }
        }

    fun sortStations(stations: List<T>): List<T>? {
        return when (coordinates) {
            null -> stations
                .onEach { it.distance = null }
                .sortedWith(compareBy({ !it.favorite }, { it.getName() }))
            else -> stations
                .onEach { it.distance = haversine(coordinates!!.latitude, coordinates!!.longitude, it.latitude, it.longitude) }
                .sortedWith(compareBy({ !it.favorite }, { it.distance }, { it.getName() }))
        }
    }

    // change this BaseStationModel to T when databinding lib is fixed
    abstract suspend fun updateFavourite(station: BaseStationModel): Int

    fun handleFavorite(position: Int) {
        viewModelScope.launch {
            val station = stations.value?.get(position) ?: throw Exception("Station is null")
            if (updateFavourite(station) > 0) {
                if (!station.favorite) getNavigator()?.showInfo(R.string.added_to_favorites)
                else getNavigator()?.showInfo(R.string.removed_from_favorites)
            }
        }
    }
}