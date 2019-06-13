package pl.sienczykm.templbn.utils

import android.content.Context
import androidx.work.*
import pl.sienczykm.templbn.background.SmogUpdateJob
import pl.sienczykm.templbn.background.SmogUpdateWorker
import pl.sienczykm.templbn.background.WeatherUpdateJob
import pl.sienczykm.templbn.background.WeatherUpdateWorker
import java.util.concurrent.TimeUnit

object UpdateHandler {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private const val AUTO_SYNC_TAG = "auto_sync_tag"
    
    fun syncNowSmogStations(context: Context) {
        SmogStation.STATIONS.map { smogStation ->
            SmogUpdateJob.enqueueWork(context, smogStation.id)
        }
    }

    fun syncNowWeatherStations(context: Context) {
        WeatherStation.STATIONS.map { weatherStation ->
            WeatherUpdateJob.enqueueWork(context, weatherStation.id)
        }
    }

    fun syncNowWeatherStation(context: Context, stationId: Int) {
        WeatherUpdateJob.enqueueWork(context, stationId)
    }

    fun syncNowSmogStation(context: Context, stationId: Int) {
        SmogUpdateJob.enqueueWork(context, stationId)
    }

    fun setAutoSync(minutes: Long) {
        WorkManager.getInstance().enqueue(SmogStation.STATIONS.map { smogStation ->
            PeriodicWorkRequestBuilder<SmogUpdateWorker>(minutes, TimeUnit.MINUTES)
                .setInputData(workDataOf(SmogStation.ID_KEY to smogStation.id))
                .setConstraints(constraints)
                .addTag(AUTO_SYNC_TAG)
                .build()
        })

        WorkManager.getInstance().enqueue(WeatherStation.STATIONS.map { weatherStation ->
            PeriodicWorkRequestBuilder<WeatherUpdateWorker>(minutes, TimeUnit.MINUTES)
                .setInputData(workDataOf(WeatherStation.ID_KEY to weatherStation.id))
                .setConstraints(constraints)
                .addTag(AUTO_SYNC_TAG)
                .build()
        })
    }

    fun disableAutoSync() {
        WorkManager.getInstance().cancelAllWorkByTag(AUTO_SYNC_TAG)
    }
}