package pl.sienczykm.templbn.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.utils.SmogStation
import pl.sienczykm.templbn.utils.WeatherStation

class AutoUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        const val STATION_ID_ARRAY_KEY = "station_id_array_key"
        const val STATION_TYPE_KEY = "station_type_key"
    }

    override fun doWork(): Result {

        val stationsId = inputData.getIntArray(STATION_ID_ARRAY_KEY)
        val stationType = inputData.getString(STATION_TYPE_KEY)

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
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}