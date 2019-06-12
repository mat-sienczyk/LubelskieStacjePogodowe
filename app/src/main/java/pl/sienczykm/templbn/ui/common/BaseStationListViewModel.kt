package pl.sienczykm.templbn.ui.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

abstract class BaseStationListViewModel<T>(application: Application) : AndroidViewModel(application) {

    abstract val stations: LiveData<List<T>>

    fun getAllStations(): LiveData<List<T>> {
        return stations
    }
}