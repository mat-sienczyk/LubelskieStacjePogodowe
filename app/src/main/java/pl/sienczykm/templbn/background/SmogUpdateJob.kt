package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import pl.sienczykm.templbn.utils.UpdateHandler

class SmogUpdateJob : UpdateJob() {

    companion object {
        private const val JOB_ID = 102

        fun enqueueWork(context: Context, stationsIds: List<Int>, statusReceiver: StatusReceiver, stationType: String) {

            val intent = Intent(context, SmogUpdateJob::class.java)
            intent.putExtra(UpdateHandler.STATION_ID_ARRAY_KEY, stationsIds.toIntArray())
            intent.putExtra(UpdateHandler.STATION_TYPE_KEY, stationType)
            intent.putExtra(RECEIVER_KEY, statusReceiver)

            enqueueWork(
                context,
                SmogUpdateJob::class.java,
                JOB_ID,
                intent
            )
        }
    }
}