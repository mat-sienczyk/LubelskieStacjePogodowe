package pl.sienczykm.templbn.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.about.AboutActivity
import pl.sienczykm.templbn.ui.list.air.AirStationListFragment
import pl.sienczykm.templbn.ui.list.weather.WeatherFragment
import pl.sienczykm.templbn.ui.map.MapFragment
import pl.sienczykm.templbn.ui.settings.SettingsActivity
import pl.sienczykm.templbn.utils.UpdateHandler

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 111
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_weather -> {
                    changeFragment(WeatherFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_air -> {
                    changeFragment(AirStationListFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    changeFragment(MapFragment.newInstance())
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

        UpdateHandler.handleAutoSync(this)

        if (savedInstanceState == null) {
            getLocationPermission()
            handleIntent()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        handleIntent()
    }

    private fun handleIntent() {
        when (intent.extras?.getString(getString(R.string.navigation_key))) {
            getString(R.string.navigation_map) -> nav_view.selectedItemId = R.id.navigation_map
            getString(R.string.navigation_air) -> nav_view.selectedItemId = R.id.navigation_air
            else -> nav_view.selectedItemId = R.id.navigation_weather
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                openSettings()
                true
            }
            R.id.about -> {
                openAboutPage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openAboutPage() {
        Intent(this, AboutActivity::class.java).run {
            startActivity(this)
        }
    }

    private fun openSettings() {
        Intent(this, SettingsActivity::class.java).run {
            startActivity(this)
        }
    }

    private fun getLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ), PERMISSIONS_REQUEST_CODE
        )
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.container, fragment)
            .commit()
    }
}
