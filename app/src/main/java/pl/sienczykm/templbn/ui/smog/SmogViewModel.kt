package pl.sienczykm.templbn.ui.smog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.SmogStationDb

class SmogViewModel(application: Application) : AndroidViewModel(application) {

    private val stations: LiveData<List<SmogStationDb>> by lazy {
        AppDb.getDatabase(getApplication()).smogStationDao().getAllStations()
    }

    fun getAllStations(): LiveData<List<SmogStationDb>> {
        return stations
    }
}