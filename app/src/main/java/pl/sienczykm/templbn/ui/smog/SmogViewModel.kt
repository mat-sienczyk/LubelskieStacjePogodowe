package pl.sienczykm.templbn.ui.smog

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.SmogStationDb
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class SmogViewModel(application: Application) : BaseStationListViewModel<SmogStationDb>(application) {

    init {
        refresh()
    }

    override fun refresh() {
        UpdateHandler.syncNowSmogStations(getApplication(), receiver)
    }

    override val stations: LiveData<List<SmogStationDb>> by lazy {
        AppDb.getDatabase(getApplication()).smogStationDao().getAllStations()
    }

}