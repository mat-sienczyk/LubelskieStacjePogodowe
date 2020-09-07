package pl.sienczykm.templbn.ui.map

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.ui.common.NavigatorAndroidViewModel

class MapViewModel(application: Application) :
    NavigatorAndroidViewModel<MapNavigator>(application) {

    private var weatherStations =
        AppDb.getDatabase(getApplication()).weatherStationDao().getAllStationsLiveData()

    private var airStations =
        AppDb.getDatabase(getApplication()).airStationDao().getAllStationsLiveData()

    val stations = MediatorLiveData<List<BaseStationModel>>()

    var weatherFilter = WeatherFilter.LOCATION
    var airFilter = AirFilter.LOCATION

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

    private fun combineLatestData() =
        (weatherStations.value ?: emptyList()) + (airStations.value ?: emptyList())

    fun setFilters(weatherFilter: WeatherFilter, airFilter: AirFilter) {
        this.weatherFilter = weatherFilter
        this.airFilter = airFilter
        getNavigator()?.updateMap()
    }

    fun onOpenFilterClicked() {
        getNavigator()?.openFilters()
    }
}