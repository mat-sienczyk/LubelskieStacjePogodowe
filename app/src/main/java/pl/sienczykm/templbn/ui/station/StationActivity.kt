package pl.sienczykm.templbn.ui.station

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.TaskStackBuilder
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.common.ActivityWithToolbarAndUpAction
import pl.sienczykm.templbn.ui.station.air.AirStationFragment
import pl.sienczykm.templbn.ui.station.weather.WeatherStationFragment
import pl.sienczykm.templbn.utils.getDefaultWeatherStationId


class StationActivity : ActivityWithToolbarAndUpAction() {

    enum class Type {
        WEATHER, AIR
    }

    companion object {
        const val STATION_TYPE_KEY = "station_type_key"
        const val STATION_ID_KEY = "station_id_key"

        fun openWeatherStationPendingIntent(
            context: Context,
            stationId: Int?
        ): PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(Intent(context, StationActivity::class.java).apply {
                putExtra(STATION_TYPE_KEY, Type.WEATHER)
                putExtra(STATION_ID_KEY, stationId ?: context.getDefaultWeatherStationId())
            })
            getPendingIntent(
                71,
                PendingIntent.FLAG_UPDATE_CURRENT
            )!! // FLAG_UPDATE_CURRENT provided, no null then
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent.getSerializableExtra(STATION_TYPE_KEY) as Type
        val stationId = intent.getIntExtra(STATION_ID_KEY, 0)

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                when (type) {
                    Type.WEATHER -> WeatherStationFragment.newInstance(stationId)
                    Type.AIR -> AirStationFragment.newInstance(stationId)
                }
            ).commit()
    }
}