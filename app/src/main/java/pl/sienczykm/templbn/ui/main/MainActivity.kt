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
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.list.air.AirStationListFragment
import pl.sienczykm.templbn.ui.list.weather.WeatherFragment
import pl.sienczykm.templbn.ui.map.MapFragment
import pl.sienczykm.templbn.ui.settings.SettingsActivity
import pl.sienczykm.templbn.utils.UpdateHandler
import pl.sienczykm.templbn.utils.isLocationPermissionGranted

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 111
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

        UpdateHandler.handleAutoSync(PreferenceManager.getDefaultSharedPreferences(this), this)

        if (savedInstanceState == null) {
            getLocation()
            changeFragment(WeatherFragment.newInstance())
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openSettings() {
        Intent(this, SettingsActivity::class.java).run {
            startActivity(this)
        }
    }

    fun getLocation() {
        if (!isLocationPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        supportFragmentManager.fragments.forEach {
            it.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.container, fragment)
            .commit()
    }
}
