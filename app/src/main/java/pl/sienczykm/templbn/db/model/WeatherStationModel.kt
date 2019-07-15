package pl.sienczykm.templbn.db.model

import androidx.room.Entity
import androidx.room.Ignore
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.utils.Config
import pl.sienczykm.templbn.utils.round
import pl.sienczykm.templbn.utils.roundAndGetString
import java.util.*

@Entity
class WeatherStationModel constructor(
    @Ignore
    override val stationId: Int,
    var type: Type,
    @Ignore
    override val name: String,
    @Ignore
    override val latitude: Double,
    @Ignore
    override val longitude: Double
) :
    BaseStationModel(stationId, name, latitude, longitude) {

    override var url: String = Config.BASE_WEATHER_URL + "podglad/" + stationId
    override var favorite: Boolean = false
    override var date: Date? = null

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

    // need to create own implementation of copy() function instead of Kotlin Data Class because of problem with inheritance
    fun copy(): WeatherStationModel {
        val stationCopy =
            WeatherStationModel(
                stationId,
                type,
                name,
                latitude,
                longitude
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

    companion object {

        val ID_KEY = "weather_station_id"

        val OGROD_BOTANICZNY = WeatherStationModel(10, Type.ONE, "Lublin - Ogród botaniczny", 51.263975, 22.514608)
        val GUCIOW = WeatherStationModel(11, Type.ONE, "Guciów", 50.582600, 23.073628)
        val FLORIANKA = WeatherStationModel(12, Type.TWO, "Florianka", 50.554803, 22.988150)
        val LUKOW = WeatherStationModel(13, Type.TWO, "Łuków", 51.930883, 22.389122)
        val PLAC_LITEWSKI = WeatherStationModel(16, Type.ONE, "Lublin - Plac Litewski", 51.248831, 22.560531)
        val ZEMBORZYCKA = WeatherStationModel(17, Type.ONE, "Lublin - MPWiK Zemborzycka", 51.203525, 22.561972)
        val HAJDOW = WeatherStationModel(18, Type.TWO, "Lublin - MPWiK Hajdów", 51.264328, 22.622867)
        val LUBARTOW = WeatherStationModel(19, Type.ONE, "PGK Lubartów", 51.452850, 22.590253)
        val TRZDNIK = WeatherStationModel(20, Type.TWO, "Trzydnik", 50.851986, 22.134056)
        val LESNIOWICE = WeatherStationModel(21, Type.TWO, "Leśniowice", 50.988278, 23.509881)
        val RYBCZEWICE = WeatherStationModel(22, Type.TWO, "Rybczewice", 51.039969, 22.853811)
        val WOLA_WERESZCZYNSKA = WeatherStationModel(23, Type.TWO, "Wola Wereszczyńska", 51.442264, 23.129692)
        val CELEJOW = WeatherStationModel(24, Type.TWO, "Celejów", 51.330653, 22.071947)

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