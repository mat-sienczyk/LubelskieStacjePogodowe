package pl.sienczykm.templbn.ui.weather

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.sienczykm.templbn.background.WeatherUpdateWorker
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.WeatherStationDb
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.WeatherStation

class WeatherViewModel(application: Application) : BaseStationListViewModel<WeatherStationDb>(application) {

    init {
        WorkManager.getInstance().enqueue(WeatherStation.STATIONS.map { weatherStation ->
            OneTimeWorkRequestBuilder<WeatherUpdateWorker>().setInputData(
                workDataOf(WeatherStation.ID_KEY to weatherStation.id)
            ).build()
        })
    }

    override val stations: LiveData<List<WeatherStationDb>> by lazy {
        AppDb.getDatabase(getApplication()).tempStationDao().getAllStations()
    }

}