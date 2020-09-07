package pl.sienczykm.templbn.ui.map

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.ui.common.NavigatorAndroidViewModel

class MapViewViewModelFactory(
    private val application: Application,
    private val weatherFilter: WeatherFilter = WeatherFilter.LOCATION,
    private val airFilter: AirFilter = AirFilter.LOCATION,
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(application, weatherFilter, airFilter) as T
    }
}

class MapViewModel(
    application: Application,
    weatherFilter: WeatherFilter,
    airFilter: AirFilter,
) :
    NavigatorAndroidViewModel<MapNavigator>(application) {

    private var weatherStations =
        AppDb.getDatabase(getApplication()).weatherStationDao().getAllStationsLiveData()

    private var airStations =
        AppDb.getDatabase(getApplication()).airStationDao().getAllStationsLiveData()

    val stations = MediatorLiveData<List<BaseStationModel>>()

    var weatherFilter = weatherFilter
        private set
    var airFilter = airFilter
        private set

    init {
        stations.apply {
            addSource(weatherStations) {
                value = combineLatestData()
            }
            addSource(airStations) {
                value = combineLatestData()
            }
        }
    }

    private fun combineLatestData(): List<BaseStationModel> {
        return (weatherStations.value ?: emptyList()) + (airStations.value ?: emptyList())
    }

    fun setFilters(weatherFilter: WeatherFilter, airFilter: AirFilter) {
        this.weatherFilter = weatherFilter
        this.airFilter = airFilter
        getNavigator()?.updateMap()
    }

    fun onOpenFilterClicked() {
        getNavigator()?.openFilters()
    }
}