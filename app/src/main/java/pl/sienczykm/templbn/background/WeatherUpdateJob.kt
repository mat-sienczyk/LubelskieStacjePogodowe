package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import pl.sienczykm.templbn.utils.UpdateHandler

class WeatherUpdateJob : UpdateJob() {

    companion object {
        private const val JOB_ID = 101

        fun enqueueWork(context: Context, stationsIds: List<Int>, statusReceiver: StatusReceiver, stationType: String) {

            val intent = Intent(context, WeatherUpdateJob::class.java)
            intent.putExtra(UpdateHandler.STATION_ID_ARRAY_KEY, stationsIds.toIntArray())
            intent.putExtra(UpdateHandler.STATION_TYPE_KEY, stationType)
            intent.putExtra(RECEIVER_KEY, statusReceiver)

            enqueueWork(
                context,
                WeatherUpdateJob::class.java,
                JOB_ID,
                intent
            )
        }
    }
}