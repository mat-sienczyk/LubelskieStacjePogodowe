package pl.sienczykm.templbn.utils

import android.content.Context
import android.os.Handler
import androidx.work.*
import pl.sienczykm.templbn.background.AutoUpdateWorker
import pl.sienczykm.templbn.background.SmogUpdateJob
import pl.sienczykm.templbn.background.StatusReceiver
import pl.sienczykm.templbn.background.WeatherUpdateJob
import java.util.concurrent.TimeUnit

object UpdateHandler {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private const val AUTO_SYNC_TAG = "auto_sync_tag"

    fun syncNowSmogStations(context: Context, receiver: StatusReceiver.Receiver) {
        SmogUpdateJob.enqueueWork(
            context,
            SmogStation.getStations().map { it.id },
            StatusReceiver(Handler(), receiver)
        )
    }

    fun syncNowWeatherStations(context: Context, receiver: StatusReceiver.Receiver) {
        WeatherUpdateJob.enqueueWork(
            context,
            WeatherStation.getStations().map { it.id },
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
        WorkManager.getInstance().enqueue(
            listOf(
                periodicWorkRequest(minutes, getStationsIntArray(SmogStation.getStations()), SmogStation.ID_KEY),
                periodicWorkRequest(minutes, getStationsIntArray(WeatherStation.getStations()), WeatherStation.ID_KEY)
            )
        )
    }

    private fun periodicWorkRequest(
        minutes: Long,
        stationsIntArray: IntArray,
        type: String
    ): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<AutoUpdateWorker>(minutes, TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    AutoUpdateWorker.STATION_ID_ARRAY_KEY to stationsIntArray,
                    AutoUpdateWorker.STATION_TYPE_KEY to type
                )
            )
            .setConstraints(constraints)
            .addTag(AUTO_SYNC_TAG)
            .build()
    }

    private fun getStationsIntArray(stations: List<Station>) = stations.map { it.id }.toIntArray()

    fun disableAutoSync() {
        WorkManager.getInstance().cancelAllWorkByTag(AUTO_SYNC_TAG)
    }
}