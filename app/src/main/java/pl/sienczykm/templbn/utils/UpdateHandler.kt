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

    const val STATION_ID_ARRAY_KEY = "station_id_array_key"
    const val STATION_TYPE_KEY = "station_type_key"
    const val AUTO_SYNC_TAG = "auto_sync_tag"

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun syncNowSmogStations(context: Context, receiver: StatusReceiver.Receiver) {
        syncNowStations(context, SmogStation.getStations().map { it.id }, receiver, SmogStation.ID_KEY)
    }

    fun syncNowWeatherStations(context: Context, receiver: StatusReceiver.Receiver) {
        syncNowStations(context, WeatherStation.getStations().map { it.id }, receiver, WeatherStation.ID_KEY)
    }

    fun syncNowSmogStation(context: Context, stationId: Int, receiver: StatusReceiver.Receiver) {
        syncNowStations(context, listOf(stationId), receiver, SmogStation.ID_KEY)
    }

    fun syncNowWeatherStation(context: Context, stationId: Int, receiver: StatusReceiver.Receiver) {
        syncNowStations(context, listOf(stationId), receiver, WeatherStation.ID_KEY)
    }

    private fun syncNowStations(
        context: Context,
        stationsIds: List<Int>,
        receiver: StatusReceiver.Receiver,
        stationType: String
    ) {
        when (stationType) {
            SmogStation.ID_KEY -> {
                SmogUpdateJob.enqueueWork(
                    context,
                    stationsIds,
                    StatusReceiver(Handler(), receiver),
                    stationType
                )
            }
            WeatherStation.ID_KEY -> {
                WeatherUpdateJob.enqueueWork(
                    context,
                    stationsIds,
                    StatusReceiver(Handler(), receiver),
                    stationType
                )
            }
        }

    }

    fun setAutoSync(minutes: Long) {
        WorkManager.getInstance().enqueue(
            listOf(
                periodicWorkRequest(60, getStationsIntArray(SmogStation.getStations()), SmogStation.ID_KEY),
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
                    STATION_ID_ARRAY_KEY to stationsIntArray,
                    STATION_TYPE_KEY to type
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