package pl.sienczykm.templbn.ui.smog

import android.app.Application
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class SmogViewModel(application: Application) : BaseStationListViewModel<SmogStationModel>(application) {

    override fun refresh() {
        UpdateHandler.syncNowSmogStations(getApplication(), receiver)
    }

    override val stations: LiveData<List<SmogStationModel>> by lazy {
        AppDb.getDatabase(getApplication()).smogStationDao().getAllStations()
    }

}