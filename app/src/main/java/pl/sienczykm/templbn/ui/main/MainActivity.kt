package pl.sienczykm.templbn.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.smog.SmogFragment
import pl.sienczykm.templbn.ui.weather.WeatherFragment
import pl.sienczykm.templbn.utils.UpdateHandler

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_smog -> {

                changeFragment(SmogFragment.newInstance())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weather -> {

                changeFragment(WeatherFragment.newInstance())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {

                UpdateHandler.disableAutoSync()

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

        UpdateHandler.setAutoSync(10)

        if (savedInstanceState == null) changeFragment(WeatherFragment.newInstance())
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
