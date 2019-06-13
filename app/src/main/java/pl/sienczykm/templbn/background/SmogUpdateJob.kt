package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import pl.sienczykm.templbn.utils.NetworkUtils
import pl.sienczykm.templbn.utils.SmogStation

class SmogUpdateJob : JobIntentService() {

    companion object {
        private const val JOB_ID = 102
        private const val RECEIVER_KEY = "receiver_key"


        fun enqueueWork(context: Context, stationId: Int, updateReceiver: UpdateReceiver) {
            enqueueWork(context, listOf(stationId), updateReceiver)
        }

        fun enqueueWork(context: Context, stationsIds: List<Int>, updateReceiver: UpdateReceiver) {

            val intent = Intent(context, SmogUpdateJob::class.java)
            intent.putExtra(SmogStation.ID_KEY, stationsIds.toIntArray())
            intent.putExtra(RECEIVER_KEY, updateReceiver)

            enqueueWork(
                context,
                SmogUpdateJob::class.java,
                JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {

        val receiver: ResultReceiver = intent.getParcelableExtra(RECEIVER_KEY)

        receiver.send(UpdateReceiver.STATUS_RUNNING, Bundle())

        if (NetworkUtils.isNetworkConnected(applicationContext)) {
            intent.getIntArrayExtra(SmogStation.ID_KEY).forEach { stationId ->
                try {
                    ProcessingUtils.updateSmogStation(applicationContext, stationId)
                    receiver.send(UpdateReceiver.STATUS_FINISHED, Bundle())
                } catch (e: Exception) {
                    val errorBundle = Bundle()
                    errorBundle.putString(ProcessingUtils.ERROR_KEY, e.localizedMessage)
                    receiver.send(UpdateReceiver.STATUS_ERROR, errorBundle)
                }
            }
        } else {
            receiver.send(UpdateReceiver.STATUS_NO_CONNECTION, Bundle())
        }
    }
}