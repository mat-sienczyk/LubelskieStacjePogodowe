package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import pl.sienczykm.templbn.utils.UpdateHandler

class AirUpdateJob : UpdateJob() {

    companion object {
        private const val JOB_ID = 102

        fun enqueueWork(context: Context, stationsIds: List<Int>, statusReceiver: StatusReceiver, stationType: String) {
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