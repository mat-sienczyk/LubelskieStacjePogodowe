package pl.sienczykm.templbn.ui.map

import androidx.annotation.StringRes
import pl.sienczykm.templbn.R

enum class WeatherFilter(@StringRes val stringId: Int) {
    NOTHING(R.string.filter_nothing),
    LOCATION(R.string.filter_location),
    TEMPERATURE(R.string.filter_temperature),
    WIND(R.string.filter_wind), // speed + dir
    HUMIDITY(R.string.filter_humidity),
    RAIN_TODAY(R.string.filter_rain_today),
}

enum class AirFilter(@StringRes val stringId: Int) {
    NOTHING(R.string.filter_nothing),
    LOCATION(R.string.filter_location),
    PM10(R.string.filter_pm10),
    PM25(R.string.filter_pm25),
    O3(R.string.filter_o3),
    NO2(R.string.filter_no2),
    SO2(R.string.filter_so2),
    C6H6(R.string.filter_c6h6),
    CO(R.string.filter_co),
}