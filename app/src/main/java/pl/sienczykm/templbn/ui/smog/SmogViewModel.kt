package pl.sienczykm.templbn.ui.smog

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.sienczykm.templbn.background.SmogUpdateWorker
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.SmogStationDb
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.SmogStation

class SmogViewModel(application: Application) : BaseStationListViewModel<SmogStationDb>(application) {

    init {
        WorkManager.getInstance().enqueue(SmogStation.STATIONS.map { smogStation ->
            OneTimeWorkRequestBuilder<SmogUpdateWorker>().setInputData(
                workDataOf(SmogStation.ID_KEY to smogStation.id)
            ).build()
        })
    }

    override val stations: LiveData<List<SmogStationDb>> by lazy {
        AppDb.getDatabase(getApplication()).smogStationDao().getAllStations()
    }

}