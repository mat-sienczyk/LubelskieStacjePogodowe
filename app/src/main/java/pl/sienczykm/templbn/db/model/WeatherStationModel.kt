package pl.sienczykm.templbn.db.model

import androidx.room.Entity
import androidx.room.Ignore
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.utils.Config
import pl.sienczykm.templbn.utils.round
import pl.sienczykm.templbn.utils.roundAndGetString
import java.text.SimpleDateFormat
import java.util.*

@Entity
class WeatherStationModel constructor(
    @Ignore
    override val stationId: Int,
    var type: Type,
    var forecastX: Int,
    var forecastY: Int,
    @Ignore
    override val latitude: Double,
    @Ignore
    override val longitude: Double,
    @Ignore
    override val city: String,
    @Ignore
    override val location: String? = null
) :
    BaseStationModel(stationId, latitude, longitude, city, location) {

    override fun getStationUrl(): String {
        return Config.BASE_WEATHER_URL + "podglad/" + stationId
    }

    override fun getOldDateTimeInMinutes(): Long = 30

    init {
        this.url = getStationUrl()
    }

    var temperature: Double? = null
    var temperatureWind: Double? = null
    var temperatureGround: Double? = null
    var windSpeed: Double? = null
    var windDir: Double? = null
    var humidity: Double? = null
    var pressure: Double? = null
    var rainToday: Double? = null
    var temperatureData: List<ChartDataModel>? = null
    var humidityData: List<ChartDataModel>? = null
    var windSpeedData: List<ChartDataModel>? = null
    var temperatureWindData: List<ChartDataModel>? = null
    var pressureData: List<ChartDataModel>? = null
    var rainTodayData: List<ChartDataModel>? = null

    //TODO set this value from preferences later
    @Ignore
    val convertWind = true

    override fun copy(): WeatherStationModel {
        val stationCopy = WeatherStationModel(
            stationId,
            type,
            forecastX,
            forecastY,
            latitude,
            longitude,
            city,
            location
        )
        stationCopy.url = url
        stationCopy.favorite = favorite
        stationCopy.date = date
        stationCopy.distance = distance

        stationCopy.temperature = temperature
        stationCopy.temperatureWind = temperatureWind
        stationCopy.temperatureGround = temperatureGround
        stationCopy.windSpeed = windSpeed
        stationCopy.windDir = windDir
        stationCopy.humidity = humidity
        stationCopy.pressure = pressure
        stationCopy.rainToday = rainToday
        stationCopy.temperatureData = temperatureData
        stationCopy.humidityData = humidityData
        stationCopy.windSpeedData = windSpeedData
        stationCopy.temperatureWindData = temperatureWindData
        stationCopy.pressureData = pressureData
        stationCopy.rainTodayData = rainTodayData
        return stationCopy
    }

    override fun isContentTheSame(other: BaseStationModel?): Boolean {
        if (super.isContentTheSame(other)) {
            other as WeatherStationModel

            if (temperature != other.temperature) return false
            if (windSpeed != other.windSpeed) return false
            if (windDir != other.windDir) return false
            if (humidity != other.humidity) return false
            if (pressure != other.pressure) return false
        }

        return super.isContentTheSame(other)
    }

    fun getParsedTemperature(roundPlaces: Int = 0): String? {
        return temperature.roundAndGetString(roundPlaces)
    }

    fun getParsedTemperatureWind(roundPlaces: Int = 0): String? {
        return temperatureWind.roundAndGetString(roundPlaces)
    }

    fun getParsedTemperatureGround(roundPlaces: Int = 0): String? {
        return temperatureGround.roundAndGetString(roundPlaces)
    }

    fun getParsedWind(roundPlaces: Int = 0): String? {
        return if (convertWind) {
            convertMetersToKm(windSpeed).roundAndGetString(roundPlaces)
        } else {
            windSpeed.roundAndGetString(roundPlaces)
        }
    }

    fun getParsedHumidity(roundPlaces: Int = 0): String? {
        return humidity.roundAndGetString(roundPlaces)
    }

    fun getParsedPressure(roundPlaces: Int = 0): String? {
        return if (pressure == 0.0) {
            pressureData?.last()?.value.roundAndGetString(roundPlaces)
        } else {
            pressure.roundAndGetString(roundPlaces)
        }
    }

    fun getParsedRain(roundPlaces: Int = 0): String? {
        return rainToday.roundAndGetString(roundPlaces)
    }

    fun convertMetersToKm(wind: Double?): Double? {
        return (wind?.times(3.6)).round(1)
    }

    fun getForecastPhotoUrl(): String {
        return "http://www.meteo.pl/um/metco/mgram_pict.php?ntype=0u&fdate=${getForecastDate()}&row=$forecastY&col=$forecastX&lang=pl"
    }

    fun getForecastUrl(): String {
        return "http://www.meteo.pl/um/php/meteorogram_map_um.php?ntype=0u&row=$forecastY&col=$forecastX&lang=pl"
    }

    fun getForecastDate(): String {
        val outputFormat = SimpleDateFormat("yyyyMMddHH", Locale("pl", "PL"))
        val now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"), Locale("pl", "PL"))
        when (now.get(Calendar.HOUR_OF_DAY)) {
            in 0..6 -> now.apply {
                set(Calendar.DAY_OF_MONTH, -1)
                set(Calendar.HOUR_OF_DAY, 18)
            }
            in 7..12 -> now.set(Calendar.HOUR_OF_DAY, 0)
            in 13..18 -> now.set(Calendar.HOUR_OF_DAY, 6)
            in 19..24 -> now.set(Calendar.HOUR_OF_DAY, 12)
        }
        return outputFormat.format(now.time)
    }

    companion object {

        val ID_KEY = "weather_station_id"

        val PLAC_LITEWSKI =
            WeatherStationModel(
                16,
                Type.ONE,
                276,
                430,
                51.248831,
                22.560531,
                "Lublin",
                "Plac Litewski"
            )
        val OGROD_BOTANICZNY =
            WeatherStationModel(
                10,
                Type.ONE,
                276,
                430,
                51.263975,
                22.514608,
                "Lublin",
                "Ogród botaniczny"
            )
        val ZEMBORZYCKA =
            WeatherStationModel(
                17,
                Type.ONE,
                276,
                430,
                51.203525,
                22.561972,
                "Lublin",
                "MPWiK Zemborzycka"
            )
        val HAJDOW =
            WeatherStationModel(
                18,
                Type.TWO,
                276,
                430,
                51.264328,
                22.622867,
                "Lublin",
                "MPWiK Hajdów"
            )
        val GUCIOW = WeatherStationModel(11, Type.ONE, 290, 451, 50.582600, 23.073628, "Guciów")
        val FLORIANKA =
            WeatherStationModel(12, Type.TWO, 283, 451, 50.554803, 22.988150, "Florianka")
        val LUKOW = WeatherStationModel(13, Type.TWO, 276, 416, 51.930883, 22.389122, "Łuków")
        val LUBARTOW =
            WeatherStationModel(19, Type.ONE, 276, 423, 51.452850, 22.590253, "Lubartów", "PGK ")
        val TRZDNIK = WeatherStationModel(20, Type.TWO, 269, 416, 50.851986, 22.134056, "Trzydnik")
        val LESNIOWICE =
            WeatherStationModel(21, Type.TWO, 297, 437, 50.988278, 23.509881, "Leśniowice")
        val RYBCZEWICE =
            WeatherStationModel(22, Type.TWO, 283, 437, 51.039969, 22.853811, "Rybczewice")
        val WOLA_WERESZCZYNSKA =
            WeatherStationModel(23, Type.TWO, 283, 423, 51.442264, 23.129692, "Wola Wereszczyńska")
        val CELEJOW = WeatherStationModel(24, Type.TWO, 269, 430, 51.330653, 22.071947, "Celejów")

        fun getStations(): List<WeatherStationModel> {
            return listOf(
                PLAC_LITEWSKI,
                OGROD_BOTANICZNY,
                ZEMBORZYCKA,
                HAJDOW,
                LUBARTOW,
                GUCIOW,
                FLORIANKA,
                LUKOW,
                TRZDNIK,
                LESNIOWICE,
                RYBCZEWICE,
                WOLA_WERESZCZYNSKA,
                CELEJOW
            )
        }

        fun getStationForGivenId(id: Int): WeatherStationModel {
            return getStations().single { it.stationId == id }
        }

        fun windIntToDir(windDirInt: Double, returnEmpty: Boolean = false): Int {
            return when {
                // north N
                windDirInt <= 22 || windDirInt >= 338 -> R.drawable.ic_arrow_down
                // north-east NE
                windDirInt <= 67 -> R.drawable.ic_arrow_bottom_left
                // east E
                windDirInt <= 112 -> R.drawable.ic_arrow_left
                // south-east SE
                windDirInt <= 157 -> R.drawable.ic_arrow_top_left
                // south S
                windDirInt <= 202 -> R.drawable.ic_arrow_up
                // south-west SW
                windDirInt <= 247 -> R.drawable.ic_arrow_top_right
                // west W
                windDirInt <= 292 -> R.drawable.ic_arrow_right
                // north-west NW
                windDirInt <= 337 -> R.drawable.ic_arrow_bottom_right
                else -> if (returnEmpty) android.R.id.empty else R.drawable.ic_wind
            }
        }
    }

    enum class Type {
        ONE, TWO
    }
}