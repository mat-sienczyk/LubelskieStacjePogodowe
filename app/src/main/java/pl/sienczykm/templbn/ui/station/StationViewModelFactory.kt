package pl.sienczykm.templbn.ui.station

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StationViewModelFactory(
    private val application: Application,
    private val type: StationFragment.Type,
    private val stationId: Int = 0
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StationViewModel(application, type, stationId) as T
    }
}