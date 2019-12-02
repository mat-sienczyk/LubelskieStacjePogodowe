package pl.sienczykm.templbn.ui.settings

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.work.ExistingPeriodicWorkPolicy
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.utils.ExternalDisplaysHandler
import pl.sienczykm.templbn.utils.UpdateHandler
import pl.sienczykm.templbn.utils.handleNightMode

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            preferenceScreen.removePreferenceRecursively("show_weather_notification_icon_key")
        }
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
            getString(R.string.night_mode_key) -> requireContext().handleNightMode()
            getString(R.string.widget_station_key), getString(R.string.widget_location_key) ->
                ExternalDisplaysHandler.updateOldWeatherWidget(requireContext())
            getString(R.string.show_weather_notification_key), getString(R.string.show_weather_notification_icon_key) ->
                ExternalDisplaysHandler.setWeatherNotification(requireContext())
            getString(R.string.enable_auto_sync_key) ->
                UpdateHandler.handleAutoSync(sharedPreferences, requireContext())
            getString(R.string.sync_via_key) -> {
                UpdateHandler.setWeatherStationAutoSync(
                    sharedPreferences,
                    requireContext(),
                    ExistingPeriodicWorkPolicy.REPLACE
                )
                UpdateHandler.setAirStationAutoSync(
                    sharedPreferences,
                    requireContext(),
                    ExistingPeriodicWorkPolicy.REPLACE
                )
            }
            getString(R.string.weather_sync_interval_key) -> UpdateHandler.setWeatherStationAutoSync(
                sharedPreferences,
                requireContext(),
                ExistingPeriodicWorkPolicy.REPLACE
            )
            getString(R.string.air_sync_interval_key) -> UpdateHandler.setAirStationAutoSync(
                sharedPreferences,
                requireContext(),
                ExistingPeriodicWorkPolicy.REPLACE
            )
        }
    }
}