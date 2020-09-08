package pl.sienczykm.templbn.ui.map

import androidx.annotation.StringRes
import pl.sienczykm.templbn.R

interface StringIdRes {
    @StringRes
    fun getStringId(): Int
}

enum class WeatherFilter : StringIdRes {
    NOTHING {
        override fun getStringId() = R.string.filter_nothing
    },
    LOCATION {
        override fun getStringId() = R.string.filter_location
    },
    TEMPERATURE {
        override fun getStringId() = R.string.filter_temperature
    },
    WIND {
        override fun getStringId() = R.string.filter_wind
    }, // speed + dir
    HUMIDITY {
        override fun getStringId() = R.string.filter_humidity
    },
    RAIN_TODAY {
        override fun getStringId() = R.string.filter_rain_today
    },
}

enum class AirFilter : StringIdRes {
    NOTHING {
        override fun getStringId() = R.string.filter_nothing
    },
    LOCATION {
        override fun getStringId() = R.string.filter_location
    },
    PM10 {
        override fun getStringId() = R.string.filter_pm10
    },
    PM25 {
        override fun getStringId() = R.string.filter_pm25
    },
    O3 {
        override fun getStringId() = R.string.filter_o3
    },
    NO2 {
        override fun getStringId() = R.string.filter_no2
    },
    SO2 {
        override fun getStringId() = R.string.filter_so2
    },
    C6H6 {
        override fun getStringId() = R.string.filter_c6h6
    },
    CO {
        override fun getStringId() = R.string.filter_co
    },
}