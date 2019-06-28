package pl.sienczykm.templbn.ui.station

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.ui.common.BaseStationViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class SmogStationViewModelFactory(
    private val application: Application,
    private val stationId: Int = 0
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SmogStationViewModel(application, stationId) as T
    }
}

class SmogStationViewModel(
    application: Application,
    private val stationId: Int = 0
) :
    BaseStationViewModel<SmogStationModel>(application, stationId) {

    override val station: LiveData<SmogStationModel> by lazy {
        AppDb.getDatabase(getApplication()).smogStationDao().getStationLiveDataById(stationId)
    }

    init {
        refresh()
    }

    override fun refresh() {
        super.refresh()
        UpdateHandler.syncNowSmogStation(getApplication(), stationId, receiver)
    }
}