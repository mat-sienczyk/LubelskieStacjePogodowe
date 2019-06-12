package pl.sienczykm.templbn.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationDb

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val stations: LiveData<List<WeatherStationDb>> by lazy {
        AppDb.getDatabase(getApplication()).tempStationDao().getAllStations()
    }

    fun getAllStations(): LiveData<List<WeatherStationDb>> {
        return stations
    }
}