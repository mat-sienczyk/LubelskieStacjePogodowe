package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.ExternalDisplaysHandler
import pl.sienczykm.templbn.utils.UpdateHandler
import pl.sienczykm.templbn.utils.isNetworkAvailable

abstract class UpdateJob : JobIntentService() {

    companion object {
        const val RECEIVER_KEY = "receiver_key"
    }

    override fun onHandleWork(intent: Intent) {

        val receiver: ResultReceiver? = intent.getParcelableExtra(RECEIVER_KEY)

        receiver?.send(StatusReceiver.STATUS_RUNNING, Bundle())

        if (applicationContext.isNetworkAvailable()) {
            intent.getIntArrayExtra(UpdateHandler.STATION_ID_ARRAY_KEY)?.forEach { stationId ->
                try {
                    when (intent.getStringExtra(UpdateHandler.STATION_TYPE_KEY)) {
                        AirStationModel.ID_KEY -> ProcessingUtils.updateAirStation(
                            applicationContext,
                            stationId
                        )
                        WeatherStationModel.ID_KEY -> ProcessingUtils.updateWeatherStation(
                            applicationContext,
                            stationId
                        )
                        else -> throw Exception("Invalid station key")
                    }
                } catch (e: Exception) {
                    val errorBundle = Bundle().apply {
                        putString(ProcessingUtils.ERROR_KEY, e.localizedMessage)
                    }
                    receiver?.send(StatusReceiver.STATUS_ERROR, errorBundle)
                }
            }
            ExternalDisplaysHandler.updateExternalDisplays(applicationContext)
        } else {
            receiver?.send(StatusReceiver.STATUS_NO_CONNECTION, Bundle())
        }

        receiver?.send(StatusReceiver.STATUS_IDLE, Bundle())
    }
}

class AirUpdateJob : UpdateJob() {

    companion object {
        private const val JOB_ID = 102

        fun enqueueWork(
            context: Context,
            stationsIds: List<Int>,
            statusReceiver: StatusReceiver,
            stationType: String
        ) {
            enqueueWork(
                context,
                AirUpdateJob::class.java,
                JOB_ID,
                Intent(context, AirUpdateJob::class.java).apply {
                    putExtra(UpdateHandler.STATION_ID_ARRAY_KEY, stationsIds.toIntArray())
                    putExtra(UpdateHandler.STATION_TYPE_KEY, stationType)
                    putExtra(RECEIVER_KEY, statusReceiver)
                }
            )
        }
    }
}

class WeatherUpdateJob : UpdateJob() {

    companion object {
        private const val JOB_ID = 101

        fun enqueueWork(
            context: Context,
            stationsIds: List<Int>,
            statusReceiver: StatusReceiver,
            stationType: String
        ) {
            enqueueWork(
                context,
                WeatherUpdateJob::class.java,
                JOB_ID,
                Intent(context, WeatherUpdateJob::class.java).apply {
                    putExtra(UpdateHandler.STATION_ID_ARRAY_KEY, stationsIds.toIntArray())
                    putExtra(UpdateHandler.STATION_TYPE_KEY, stationType)
                    putExtra(RECEIVER_KEY, statusReceiver)
                }
            )
        }
    }
}