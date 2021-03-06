package pl.sienczykm.templbn.ui.station.air

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.ui.station.common.BaseStationViewModel
import pl.sienczykm.templbn.utils.UpdateHandler

class AirStationViewModelFactory(
    private val application: Application,
    private val stationId: Int = 0
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AirStationViewModel(application, stationId) as T
    }
}

class AirStationViewModel(
    application: Application,
    private val stationId: Int = 0
) :
    BaseStationViewModel<AirStationModel>(application, stationId) {

    override val station =
        AppDb.getDatabase(getApplication()).airStationDao().getStationLiveDataById(stationId)

    init {
        refresh()
    }

    override fun refresh() {
        super.refresh()
        UpdateHandler.syncNowAirStation(getApplication(), stationId, receiver)
    }

    override suspend fun updateFavourite(station: AirStationModel): Int =
        AppDb.getDatabase(getApplication()).airStationDao().updateFavoriteSuspend(
            station.stationId, !station.favorite
        )

    fun showChart(
        chartData: List<ChartDataModel>?,
        healthyValue: Float,
        unit: String,
        @StringRes titleId: Int
    ) {
        chartData?.let {
            getNavigator()?.showChart(
                chartData = it,
                minIsZero = true,
                limitValue = healthyValue,
                unit = unit,
                titleId = titleId
            )
        }
    }
}