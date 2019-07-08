package pl.sienczykm.templbn.ui.station

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R


class StationActivity : AppCompatActivity(){

    enum class Type {
        WEATHER, AIR
    }

    companion object {
        const val STATION_TYPE_KEY = "station_type_key"
        const val STATION_ID_KEY = "station_id_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_with_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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