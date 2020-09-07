package pl.sienczykm.templbn.ui.map

enum class WeatherFilter {
    NOTHING,
    LOCATION,
    TEMPERATURE,
    WIND, // speed + dir
    HUMIDITY,
    RAIN_TODAY
}

enum class AirFilter {
    NOTHING,
    LOCATION,
    PM10,
    PM25,
    O3,
    NO2,
    SO2,
    C6H6,
    CO
}