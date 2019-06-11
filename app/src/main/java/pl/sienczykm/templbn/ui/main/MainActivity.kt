package pl.sienczykm.templbn.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.background.GetStationsWorker
import pl.sienczykm.templbn.background.SmogUpdateWorker
import pl.sienczykm.templbn.background.WeatherUpdateWorker
import pl.sienczykm.templbn.utils.SmogStation
import pl.sienczykm.templbn.utils.WeatherStation

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_smog -> {

                WorkManager.getInstance().enqueue(SmogStation.STATIONS.map { smogStation ->
                    OneTimeWorkRequestBuilder<SmogUpdateWorker>().setInputData(
                        workDataOf(SmogStation.ID_KEY to smogStation.id)
                    ).build()
                })

//                changeFragment(SmogFragment.newInstance())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weather -> {

                WorkManager.getInstance().enqueue(WeatherStation.STATIONS.map { weatherStation ->
                    OneTimeWorkRequestBuilder<WeatherUpdateWorker>().setInputData(
                        workDataOf(WeatherStation.ID_KEY to weatherStation.id)
                    ).build()
                })

//                changeFragment(WeatherFragment.newInstance())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {

                WorkManager.getInstance().enqueue(OneTimeWorkRequestBuilder<GetStationsWorker>().build())

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.container, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }
}
