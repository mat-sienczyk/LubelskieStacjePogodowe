package pl.sienczykm.templbn.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.work.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.background.AirUpdateJob
import pl.sienczykm.templbn.background.AutoUpdateWorker
import pl.sienczykm.templbn.background.StatusReceiver
import pl.sienczykm.templbn.background.WeatherUpdateJob
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import java.util.concurrent.TimeUnit

object UpdateHandler {

    const val STATION_ID_ARRAY_KEY = "station_id_array_key"
    const val STATION_TYPE_KEY = "station_type_key"
    const val AUTO_SYNC_TAG = "auto_sync_tag"
    const val AIR_SYNC_WORK_NAME = "air_work_name"
    const val FREQUENT_WEATHER_SYNC_WORK_NAME = "frequent_weather_work_name"
    const val RARE_WEATHER_SYNC_WORK_NAME = "rare_weather_work_name"

    fun syncNowAirStations(
        context: Context,
        receiver: StatusReceiver.Receiver,
        stations: List<AirStationModel> = AirStationModel.getAllStations(),
    ) {
        syncNowStations(
            context,
            stations.map { it.stationId },
            receiver,
            AirStationModel.ID_KEY
        )
    }

    fun syncNowAirStation(context: Context, stationId: Int, receiver: StatusReceiver.Receiver) {
        syncNowStations(context, listOf(stationId), receiver, AirStationModel.ID_KEY)
    }

    fun syncNowWeatherStations(
        context: Context,
        receiver: StatusReceiver.Receiver,
        stations: List<WeatherStationModel> = WeatherStationModel.getAllStations(),
    ) {
        syncNowStations(
            context,
            stations.map { it.stationId },
            receiver,
            WeatherStationModel.ID_KEY
        )
    }

    fun syncNowWeatherStation(context: Context, stationId: Int, receiver: StatusReceiver.Receiver) {
        syncNowStations(context, listOf(stationId), receiver, WeatherStationModel.ID_KEY)
    }

    private fun syncNowStations(
        context: Context,
        stationsIds: List<Int>,
        receiver: StatusReceiver.Receiver,
        stationType: String,
    ) {
        when (stationType) {
            AirStationModel.ID_KEY -> {
                AirUpdateJob.enqueueWork(
                    context,
                    stationsIds,
                    StatusReceiver(Handler(Looper.getMainLooper()), receiver),
                    stationType
                )
            }
            WeatherStationModel.ID_KEY -> {
                WeatherUpdateJob.enqueueWork(
                    context,
                    stationsIds,
                    StatusReceiver(Handler(Looper.getMainLooper()), receiver),
                    stationType
                )
            }
        }

    }

    fun handleAutoSync(context: Context) {
        if (context.isAutoUpdateEnabled()) {
            setWeatherStationAutoSync(context, ExistingPeriodicWorkPolicy.KEEP)
            setAirStationAutoSync(context, ExistingPeriodicWorkPolicy.KEEP)
        } else disableAutoSync(context)
    }

    fun setWeatherStationAutoSync(
        context: Context,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
    ) {

        val weatherUpdateInterval = context.getWeatherStationUpdateInterval().toLong()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            FREQUENT_WEATHER_SYNC_WORK_NAME, existingPeriodicWorkPolicy, periodicWorkRequest(
                weatherUpdateInterval,
                WeatherStationModel.getFrequentUpdatedStations().map { it.stationId }.toIntArray(),
                WeatherStationModel.ID_KEY,
                syncViaWifiOnly(context)
            )
        )
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            RARE_WEATHER_SYNC_WORK_NAME, existingPeriodicWorkPolicy, periodicWorkRequest(
                weatherUpdateInterval.run { if (this < 60) 60 else this },
                WeatherStationModel.getRareUpdatedStations().map { it.stationId }.toIntArray(),
                WeatherStationModel.ID_KEY,
                syncViaWifiOnly(context)
            )
        )
    }

    fun setAirStationAutoSync(
        context: Context,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
    ) {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            AIR_SYNC_WORK_NAME, existingPeriodicWorkPolicy, periodicWorkRequest(
                context.getAirStationUpdateInterval().toLong(),
                AirStationModel.getAllStations().map { it.stationId }.toIntArray(),
                AirStationModel.ID_KEY,
                syncViaWifiOnly(context)
            )
        )
    }

    fun disableAutoSync(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(AUTO_SYNC_TAG)
        ExternalDisplaysHandler.cancelAllNotifications(context) //TODO: ugly?
    }

    private fun periodicWorkRequest(
        minutes: Long,
        stationsIntArray: IntArray,
        type: String,
        onlyWifi: Boolean,
    ): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<AutoUpdateWorker>(minutes, TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    STATION_ID_ARRAY_KEY to stationsIntArray,
                    STATION_TYPE_KEY to type
                )
            )
            .setConstraints(buildConstraints(onlyWifi))
            .addTag(AUTO_SYNC_TAG)
            .build()
    }

    private fun buildConstraints(onlyWifi: Boolean) = Constraints.Builder()
        .setRequiredNetworkType(if (onlyWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
        .build()

    private fun syncViaWifiOnly(context: Context): Boolean {
        return when (context.getSyncVia()) {
            context.getString(R.string.sync_default) -> false
            else -> true
        }
    }
}

