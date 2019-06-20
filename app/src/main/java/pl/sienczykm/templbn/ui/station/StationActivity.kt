package pl.sienczykm.templbn.ui.station

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R


class StationActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val type = intent.getSerializableExtra(StationFragment.STATION_TYPE_KEY) as StationFragment.Type
        val stationId = intent.getIntExtra(StationFragment.STATION_ID_KEY, 0)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, StationFragment.newInstance(type, stationId))
        transaction.commit()
    }
}