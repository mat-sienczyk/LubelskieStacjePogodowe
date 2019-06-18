package pl.sienczykm.templbn.background

import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.NetworkUtils
import pl.sienczykm.templbn.utils.UpdateHandler

abstract class UpdateJob : JobIntentService() {

    companion object {
        const val RECEIVER_KEY = "receiver_key"
    }

    override fun onHandleWork(intent: Intent) {

        val receiver: ResultReceiver = intent.getParcelableExtra(RECEIVER_KEY)

        receiver.send(StatusReceiver.STATUS_RUNNING, Bundle())

        if (NetworkUtils.isNetworkConnected(applicationContext)) {
            intent.getIntArrayExtra(UpdateHandler.STATION_ID_ARRAY_KEY).forEach { stationId ->
                try {
                    when (intent.getStringExtra(UpdateHandler.STATION_TYPE_KEY)) {
                        SmogStationModel.ID_KEY -> ProcessingUtils.updateSmogStation(
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
                    val errorBundle = Bundle()
                    errorBundle.putString(ProcessingUtils.ERROR_KEY, e.localizedMessage)
                    receiver.send(StatusReceiver.STATUS_ERROR, errorBundle)
                }
            }
        } else {
            receiver.send(StatusReceiver.STATUS_NO_CONNECTION, Bundle())
        }

        receiver.send(StatusReceiver.STATUS_IDLE, Bundle())
    }
}