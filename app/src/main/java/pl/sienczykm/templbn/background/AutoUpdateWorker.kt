package pl.sienczykm.templbn.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.utils.SmogStation
import pl.sienczykm.templbn.utils.UpdateHandler
import pl.sienczykm.templbn.utils.WeatherStation

class AutoUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val stationsId = inputData.getIntArray(UpdateHandler.STATION_ID_ARRAY_KEY)
        val stationType = inputData.getString(UpdateHandler.STATION_TYPE_KEY)

        return try {
            when (stationType) {
                SmogStation.ID_KEY -> {
                    stationsId?.forEach { stationId ->
                        ProcessingUtils.updateSmogStation(
                            applicationContext,
                            stationId
                        )
                    }
                }
                WeatherStation.ID_KEY -> {
                    stationsId?.forEach { stationId ->
                        ProcessingUtils.updateWeatherStation(
                            applicationContext,
                            stationId
                        )
                    }
                }
                else -> throw Exception("Invalid station key")
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}