package pl.sienczykm.templbn.utils

import androidx.work.*
import pl.sienczykm.templbn.background.SmogUpdateWorker
import pl.sienczykm.templbn.background.WeatherUpdateWorker
import java.util.concurrent.TimeUnit

object UpdateHandler {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private const val AUTO_SYNC_TAG = "auto_sync_tag"

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