package pl.sienczykm.templbn.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.utils.WeatherStation

class WeatherUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val stationId = inputData.getInt(WeatherStation.ID_KEY, 0)

        return if (stationId != 0) {
            WeatherProcessingUtils.updateWeatherStation(applicationContext, stationId)

            Result.success()
        } else {
            Result.failure()
        }
    }
}