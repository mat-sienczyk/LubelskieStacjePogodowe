package pl.sienczykm.templbn.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.smog.SmogFragment
import pl.sienczykm.templbn.ui.weather.WeatherFragment

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

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

//                WorkManager.getInstance().enqueue(OneTimeWorkRequestBuilder<GetStationsWorker>().build())

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

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (savedInstanceState == null) changeFragment(WeatherFragment.newInstance())
    }

    fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.container, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }
}
