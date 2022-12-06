package pl.sienczykm.templbn.ui.settings

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.ExistingPeriodicWorkPolicy
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.*

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {

        const val BG_LOCATION_PERMISSIONS_REQUEST_CODE = 536

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        preferenceManager.findPreference<ListPreference>(getString(R.string.default_station_key))
            ?.apply {
                entries =
                    WeatherStationModel.getAllStations().map { it.getFullStationName().toString() }
                        .toTypedArray()
                entryValues = WeatherStationModel.getAllStations().map { it.stationId.toString() }
                    .toTypedArray()
            }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            preferenceScreen.removePreferenceRecursively(getString(R.string.show_weather_notification_icon_key))
        }
        if (!requireContext().isGooglePlayServicesAvailable()) {
            preferenceScreen.removePreferenceRecursively(getString(R.string.enable_google_play_services_key))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && requireContext().useLocationToUpdateWeather() && !requireContext().isBgLocationPermissionGranted()) {
            preferenceManager.findPreference<SwitchPreferenceCompat>(getString(R.string.default_location_key))?.isChecked =
                false
        }
    }

    override fun onStart() {
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            //TODO use Activity Result APIs in the future
            getString(R.string.enable_google_play_services_key) ->
                (requireActivity() as SettingsActivity).reloadMapFragment()

            getString(R.string.night_mode_key) ->
                requireContext().handleNightMode()

            getString(R.string.default_station_key),
            getString(R.string.open_weather_station_from_externals_key),
            ->
                ExternalDisplaysHandler.updateExternalDisplays(requireContext())

            getString(R.string.default_location_key) -> {
                handleBgLocationRequest()
                ExternalDisplaysHandler.updateExternalDisplays(requireContext())
            }

            getString(R.string.show_weather_notification_key),
            getString(R.string.show_weather_notification_icon_key),
            ->
                ExternalDisplaysHandler.setWeatherNotification(requireContext())

            getString(R.string.show_air_quality_notification_key) ->
                ExternalDisplaysHandler.checkAirQuality(requireContext())

            getString(R.string.enable_auto_sync_key) ->
                UpdateHandler.handleAutoSync(requireContext())

            getString(R.string.sync_via_key) -> {
                UpdateHandler.setWeatherStationAutoSync(
                    requireContext(),
                    ExistingPeriodicWorkPolicy.REPLACE
                )
                UpdateHandler.setAirStationAutoSync(
                    requireContext(),
                    ExistingPeriodicWorkPolicy.REPLACE
                )
            }

            getString(R.string.weather_sync_interval_key) ->
                UpdateHandler.setWeatherStationAutoSync(
                    requireContext(),
                    ExistingPeriodicWorkPolicy.REPLACE
                )

            getString(R.string.air_sync_interval_key) ->
                UpdateHandler.setAirStationAutoSync(
                    requireContext(),
                    ExistingPeriodicWorkPolicy.REPLACE
                )
        }
    }

    // TODO replace below code with registerForActivityResult() from 'androidx.activity:activity-ktx:1.2.0' and 'androidx.fragment:fragment-ktx:1.3.0'
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == BG_LOCATION_PERMISSIONS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            preferenceManager.findPreference<SwitchPreferenceCompat>(getString(R.string.default_location_key))?.isChecked =
                false
            requireView().showSnackbar(
                message = R.string.no_bg_location,
                buttonText = R.string.settings,
                action = {
                    requireContext().startActivity(Intent().apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    })
                },
            )
        }
    }

    private fun handleBgLocationRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && requireContext().useLocationToUpdateWeather() && !requireContext().isBgLocationPermissionGranted()) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BG_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }
}
