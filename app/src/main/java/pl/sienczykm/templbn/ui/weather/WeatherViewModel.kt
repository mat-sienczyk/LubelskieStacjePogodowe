package pl.sienczykm.templbn.ui.weather

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherViewModel(application: Application) : BaseStationListViewModel<WeatherStationModel>(application) {

    override fun refresh() {
        UpdateHandler.syncNowWeatherStations(getApplication(), receiver)
    }

    override val stationsLiveData: LiveData<List<WeatherStationModel>> by lazy {
        AppDb.getDatabase(getApplication()).weatherStationDao().getAllStationsLiveData()
    }

    init {
        stations.addSource(stationsLiveData) { result: List<WeatherStationModel>? ->
            result?.let {
                stations.value = sortStations(it)
            }
        }
    }
}