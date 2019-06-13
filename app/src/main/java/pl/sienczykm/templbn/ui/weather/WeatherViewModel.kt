package pl.sienczykm.templbn.ui.weather

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationDb
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel

class WeatherViewModel(application: Application) : BaseStationListViewModel<WeatherStationDb>(application) {

    override val stations: LiveData<List<WeatherStationDb>> by lazy {
        AppDb.getDatabase(getApplication()).tempStationDao().getAllStations()
    }

}