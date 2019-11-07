package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherUpdateJob : UpdateJob() {

    companion object {
        private const val JOB_ID = 101

        fun enqueueWork(context: Context, stationsIds: List<Int>, statusReceiver: StatusReceiver, stationType: String) {
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