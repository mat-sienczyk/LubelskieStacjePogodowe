package pl.sienczykm.templbn.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.TempStationDb

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val stations: LiveData<List<TempStationDb>> by lazy {
        AppDb.getDatabase(getApplication()).tempStationDao().getAllStations()
    }

    fun getAllStations(): LiveData<List<TempStationDb>> {
        return stations
    }
}