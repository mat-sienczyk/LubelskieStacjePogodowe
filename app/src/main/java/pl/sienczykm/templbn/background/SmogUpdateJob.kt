package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import pl.sienczykm.templbn.utils.SmogStation

class SmogUpdateJob : JobIntentService() {

    companion object {
        private const val JOB_ID = 102

        fun enqueueWork(context: Context, stationId: Int) {
            enqueueWork(
                context,
                SmogUpdateJob::class.java,
                JOB_ID,
                Intent(context, SmogUpdateJob::class.java).putExtra(SmogStation.ID_KEY, stationId)
            )
        }
    }

    override fun onHandleWork(intent: Intent) {

        val stationId = intent.getIntExtra(SmogStation.ID_KEY, 0)

        if (stationId != 0) {
            SmogProcessingUtils.updateSmogStation(applicationContext, stationId)
        } else {
            SmogProcessingUtils.updateSmogStation(applicationContext, 16)
        }
    }
}