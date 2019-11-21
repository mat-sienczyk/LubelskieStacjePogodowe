package pl.sienczykm.templbn.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.UpdateHandler
import pl.sienczykm.templbn.utils.updateOldWeatherWidget

class AutoUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val stationsId = inputData.getIntArray(UpdateHandler.STATION_ID_ARRAY_KEY)
        val stationType = inputData.getString(UpdateHandler.STATION_TYPE_KEY)

        return try {
            when (stationType) {
                AirStationModel.ID_KEY -> {
                    stationsId?.forEach { stationId ->
                        ProcessingUtils.updateAirStation(
                            applicationContext,
                            stationId
                        )
                    }
                }
                WeatherStationModel.ID_KEY -> {
                    stationsId?.forEach { stationId ->
                        ProcessingUtils.updateWeatherStation(
                            applicationContext,
                            stationId
                        )
                    }
                    applicationContext.updateOldWeatherWidget()
                }
                else -> throw Exception("Invalid station key")
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}