package pl.sienczykm.templbn.background

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import pl.sienczykm.templbn.utils.WeatherStation

class WeatherUpdateJob : JobIntentService() {

    companion object {
        private const val JOB_ID = 101

        fun enqueueWork(context: Context, stationId: Int) {
            enqueueWork(
                context,
                WeatherUpdateJob::class.java,
                JOB_ID,
                Intent(context, WeatherUpdateJob::class.java).putExtra(WeatherStation.ID_KEY, stationId)
            )
        }
    }

    override fun onHandleWork(intent: Intent) {

        val stationId = intent.getIntExtra(WeatherStation.ID_KEY, 0)

        if (stationId != 0) {
            WeatherProcessingUtils.updateWeatherStation(applicationContext, stationId)
        } else {
            WeatherProcessingUtils.updateWeatherStation(applicationContext, 16)
        }
    }
}