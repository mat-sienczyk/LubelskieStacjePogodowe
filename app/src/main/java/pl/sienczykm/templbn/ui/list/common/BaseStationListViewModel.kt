package pl.sienczykm.templbn.ui.list.common

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.ui.common.BaseNavigator
import pl.sienczykm.templbn.ui.common.BaseRefreshViewModel
import pl.sienczykm.templbn.utils.haversine

abstract class BaseStationListViewModel<T : BaseStationModel>(application: Application) :
    BaseRefreshViewModel<BaseNavigator>(application) {

    val stations = MediatorLiveData<List<T>>()

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
}