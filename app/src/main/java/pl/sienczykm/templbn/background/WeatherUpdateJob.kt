package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import pl.sienczykm.templbn.utils.NetworkUtils
import pl.sienczykm.templbn.utils.WeatherStation

class WeatherUpdateJob : JobIntentService() {

    companion object {
        private const val JOB_ID = 101
        private const val RECEIVER_KEY = "receiver_key"


        fun enqueueWork(context: Context, stationId: Int, statusReceiver: StatusReceiver) {
            enqueueWork(context, listOf(stationId), statusReceiver)
        }

        fun enqueueWork(context: Context, stationsIds: List<Int>, statusReceiver: StatusReceiver) {

            val intent = Intent(context, WeatherUpdateJob::class.java)
            intent.putExtra(WeatherStation.ID_KEY, stationsIds.toIntArray())
            intent.putExtra(RECEIVER_KEY, statusReceiver)

            enqueueWork(
                context,
                WeatherUpdateJob::class.java,
                JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {

        val receiver: ResultReceiver = intent.getParcelableExtra(RECEIVER_KEY)

        receiver.send(StatusReceiver.STATUS_RUNNING, Bundle())

        if (NetworkUtils.isNetworkConnected(applicationContext)) {
            intent.getIntArrayExtra(WeatherStation.ID_KEY).forEach { stationId ->
                try {
                    ProcessingUtils.updateWeatherStation(applicationContext, stationId)
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