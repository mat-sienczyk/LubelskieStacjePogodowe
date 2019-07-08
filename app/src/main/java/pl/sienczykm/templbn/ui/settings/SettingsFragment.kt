package pl.sienczykm.templbn.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.utils.Constants
import pl.sienczykm.templbn.utils.UpdateHandler

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            getString(R.string.enable_auto_sync_key) -> UpdateHandler.handleAutoSync(
                sharedPreferences.getBoolean(key, false),
                sharedPreferences.getString(
                    getString(R.string.weather_sync_interval_key),
                    Constants.WEATHER_DEFAULT_INTERVAL
                ).toLong(),
                sharedPreferences.getString(
                    getString(R.string.air_sync_interval_key),
                    Constants.AIR_DEFAULT_INTERVAL
                ).toLong()
            )
            getString(R.string.weather_sync_interval_key) -> UpdateHandler.setWeatherStationAutoSync(
                sharedPreferences.getString(key, Constants.WEATHER_DEFAULT_INTERVAL).toLong()
            )
            getString(R.string.air_sync_interval_key) -> UpdateHandler.setAirStationAutoSync(
                sharedPreferences.getString(key, Constants.AIR_DEFAULT_INTERVAL).toLong()
            )
        }
    }
}