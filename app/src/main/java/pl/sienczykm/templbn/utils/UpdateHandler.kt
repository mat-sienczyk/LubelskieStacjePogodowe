package pl.sienczykm.templbn.utils

import android.content.Context
import android.os.Handler
import androidx.work.*
import pl.sienczykm.templbn.background.*
import java.util.concurrent.TimeUnit

object UpdateHandler {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private const val AUTO_SYNC_TAG = "auto_sync_tag"

    fun syncNowSmogStations(context: Context, receiver: StatusReceiver.Receiver) {
        SmogUpdateJob.enqueueWork(
            context,
            SmogStation.STATIONS.map { it.id },
            StatusReceiver(Handler(), receiver)
        )
    }

    fun syncNowWeatherStations(context: Context, receiver: StatusReceiver.Receiver) {
        WeatherUpdateJob.enqueueWork(
            context,
            WeatherStation.STATIONS.map { it.id },
            StatusReceiver(Handler(), receiver)
        )

    }

    fun syncNowWeatherStation(context: Context, stationId: Int, receiver: StatusReceiver.Receiver) {
        WeatherUpdateJob.enqueueWork(context, stationId, StatusReceiver(Handler(), receiver))
    }

    fun syncNowSmogStation(context: Context, stationId: Int, receiver: StatusReceiver.Receiver) {
        SmogUpdateJob.enqueueWork(context, stationId, StatusReceiver(Handler(), receiver))
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