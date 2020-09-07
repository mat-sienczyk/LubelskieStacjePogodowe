package pl.sienczykm.templbn.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.BoolRes
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import pl.sienczykm.templbn.BuildConfig
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.map.AirFilter
import pl.sienczykm.templbn.ui.map.WeatherFilter

private fun Context.getDefaultSharedPreferences(): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(this)

private fun Context.getPreferenceBoolean(@StringRes key: Int, @BoolRes defaultValue: Int) =
    getPreferenceBoolean(getString(key), resources.getBoolean(defaultValue))

private fun Context.getPreferenceBoolean(key: String, defaultValue: Boolean) =
    getDefaultSharedPreferences().getBoolean(key, defaultValue)

private fun Context.getPreferenceInt(@StringRes key: Int, @StringRes defaultValue: Int) =
    getDefaultSharedPreferences().getString(
        getString(key),
        getString(defaultValue)
    )!!.toInt() // default value provided

private fun Context.getPreferenceString(@StringRes key: Int, @StringRes defaultValue: Int) =
    getDefaultSharedPreferences().getString(
        getString(key),
        getString(defaultValue)
    )!! // default value provided

private fun Context.getPreferenceString(@StringRes key: Int, defaultValue: String) =
    getDefaultSharedPreferences().getString(
        getString(key),
        defaultValue
    )!! // default value provided

fun Context.isAutoUpdateEnabled() =
    getPreferenceBoolean(R.string.enable_auto_sync_key, R.bool.auto_sync_default)

fun Context.createIconForNotification() =
    getPreferenceBoolean(
        R.string.show_weather_notification_icon_key,
        R.bool.show_weather_notification_icon_default
    )

fun Context.useLocationToUpdateWeather() =
    getPreferenceBoolean(R.string.default_location_key, R.bool.default_location_default)

fun Context.showAirQualityNotification(): Boolean {
    return isAutoUpdateEnabled() && getPreferenceBoolean(
        R.string.show_air_quality_notification_key,
        R.bool.show_air_quality_notification_default
    )
}

fun Context.showWeatherNotification(): Boolean {
    return isAutoUpdateEnabled() && getPreferenceBoolean(
        R.string.show_weather_notification_key,
        R.bool.show_weather_notification_default
    )
}

fun Context.openWeatherStation() =
    getPreferenceBoolean(
        R.string.open_weather_station_from_externals_key,
        R.bool.open_weather_station_from_externals_default
    )

fun Context.getSyncVia() =
    getPreferenceString(R.string.sync_via_key, R.string.sync_default)

fun Context.getDarkModeSetting() =
    getPreferenceInt(R.string.night_mode_key, R.string.night_mode_default)

fun Context.getAirQualityLevel() =
    getPreferenceInt(R.string.air_quality_warning_key, R.string.air_quality_warning_default)

fun Context.getDefaultWeatherStationId() =
    getPreferenceInt(R.string.default_station_key, R.string.default_station_default)

fun Context.getWeatherStationUpdateInterval() =
    getPreferenceInt(R.string.weather_sync_interval_key, R.string.weather_sync_interval_default)

fun Context.getAirStationUpdateInterval() =
    getPreferenceInt(R.string.air_sync_interval_key, R.string.air_sync_interval_default)

fun Context.useGooglePlayServices() = getPreferenceBoolean(
    R.string.enable_google_play_services_key,
    R.bool.enable_google_play_services_default
)

fun Context.isNewVersion(): Boolean {
    val toReturn = getPreferenceBoolean(BuildConfig.VERSION_NAME, true)
    getDefaultSharedPreferences().edit().putBoolean(BuildConfig.VERSION_NAME, false).apply()
    return toReturn
}

fun Context.getWeatherFilter() =
    WeatherFilter.valueOf(getPreferenceString(R.string.weather_filter_key,
        WeatherFilter.LOCATION.name))

fun Context.setWeatherFilter(filter: WeatherFilter) =
    getDefaultSharedPreferences().edit()
        .putString(getString(R.string.weather_filter_key), filter.name).apply()

fun Context.getAirFilter() =
    AirFilter.valueOf(getPreferenceString(R.string.air_filter_key,
        AirFilter.LOCATION.name))

fun Context.setAirFilter(filter: AirFilter) =
    getDefaultSharedPreferences().edit()
        .putString(getString(R.string.air_filter_key), filter.name).apply()

