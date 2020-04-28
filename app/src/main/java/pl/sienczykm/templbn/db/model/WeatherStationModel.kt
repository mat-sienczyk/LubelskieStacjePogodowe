package pl.sienczykm.templbn.db.model

import androidx.room.Entity
import androidx.room.Ignore
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.utils.*
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
        return when (type) {
            Type.UMCS_ONE, Type.UMCS_TWO -> Config.UMCS_BASE_WEATHER_URL + "podglad/" + stationId
            Type.IMGW_POGODYNKA -> Config.POGODYNKA_BASE_WEATHER_URL + "#station/meteo/" + stationId
            else -> ""
        }
    }

    override fun getStationSource(): Int = when (type) {
        Type.UMCS_ONE, Type.UMCS_TWO -> R.string.umcs_station_title_weather
        Type.IMGW_POGODYNKA, Type.IMGW_SIMPLE -> R.string.imgw_station_title_weather
    }

    override fun getOldDateTimeInMinutes(): Long = when (type) {
        Type.UMCS_ONE, Type.UMCS_TWO -> 30
        Type.IMGW_POGODYNKA, Type.IMGW_SIMPLE -> 90
    }

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
        return getLatestParsedData(roundPlaces, temperature, temperatureData)
    }

    fun getParsedTemperatureWind(roundPlaces: Int = 0): String? {
        return getLatestParsedData(roundPlaces, temperatureWind, temperatureWindData)
    }

    fun getParsedTemperatureGround(roundPlaces: Int = 0): String? {
        return getLatestParsedData(roundPlaces, temperatureGround, null)
    }

    fun getParsedWind(roundPlaces: Int = 0): String? {
        return getLatestParsedData(roundPlaces, windSpeed, windSpeedData, convertWind)
    }

    fun getParsedHumidity(roundPlaces: Int = 0): String? {
        return getLatestParsedData(roundPlaces, humidity, humidityData)
    }

    fun getParsedPressure(roundPlaces: Int = 0): String? {
        return if (pressure == 0.0) {
            pressureData?.lastOrNull()?.value.roundAndGetString(roundPlaces)
        } else {
            getLatestParsedData(roundPlaces, pressure, pressureData)
        }
    }

    fun getParsedRain(roundPlaces: Int = 0): String? {
        return getLatestParsedData(roundPlaces, rainToday, rainTodayData)
    }

    fun getProperWindSpeedData(): List<ChartDataModel>? {
        return if (convertWind) {
            windSpeedData?.map { ChartDataModel(it.timestamp, convertMetersToKm(it.value)) }
        } else windSpeedData
    }

    private fun getLatestParsedData(
        roundPlaces: Int,
        data: Double?,
        chartData: List<ChartDataModel>?,
        convertWind: Boolean = false
    ): String? {
        return if (convertWind) {
            convertMetersToKm(data).roundAndGetString(roundPlaces)
                ?: convertMetersToKm(chartData?.lastOrNull()?.value).roundAndGetString(roundPlaces)
        } else {
            data.roundAndGetString(roundPlaces)
                ?: chartData?.lastOrNull()?.value.roundAndGetString(roundPlaces)
        }
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
        val now = nowInPoland()
        when (now.get(Calendar.HOUR_OF_DAY)) {
            in 0..6 -> now.apply {
                set(Calendar.DAY_OF_MONTH, -1)
                set(Calendar.HOUR_OF_DAY, 18)
            }
            in 7..12 -> now.set(Calendar.HOUR_OF_DAY, 0)
            in 13..18 -> now.set(Calendar.HOUR_OF_DAY, 6)
            in 19..24 -> now.set(Calendar.HOUR_OF_DAY, 12)
        }
        return dateFormat("yyyyMMddHH").format(now.time)
    }

    companion object {

        const val ID_KEY = "weather_station_id"

        // UMCS weather stations
        val PLAC_LITEWSKI =
            WeatherStationModel(
                16,
                Type.UMCS_ONE,
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
                Type.UMCS_ONE,
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
                Type.UMCS_ONE,
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
                Type.UMCS_TWO,
                276,
                430,
                51.264328,
                22.622867,
                "Lublin",
                "MPWiK Hajdów"
            )
        val GUCIOW =
            WeatherStationModel(11, Type.UMCS_ONE, 290, 451, 50.582600, 23.073628, "Guciów")
        val FLORIANKA =
            WeatherStationModel(12, Type.UMCS_TWO, 283, 451, 50.554803, 22.988150, "Florianka")
        val LUKOW = WeatherStationModel(13, Type.UMCS_TWO, 276, 416, 51.930883, 22.389122, "Łuków")
        val LUBARTOW =
            WeatherStationModel(
                19,
                Type.UMCS_ONE,
                276,
                423,
                51.452850,
                22.590253,
                "Lubartów",
                "PGK "
            )
        val TRZDNIK =
            WeatherStationModel(20, Type.UMCS_TWO, 269, 416, 50.851986, 22.134056, "Trzydnik")
        val LESNIOWICE =
            WeatherStationModel(21, Type.UMCS_TWO, 297, 437, 50.988278, 23.509881, "Leśniowice")
        val RYBCZEWICE =
            WeatherStationModel(22, Type.UMCS_TWO, 283, 437, 51.039969, 22.853811, "Rybczewice")
        val WOLA_WERESZCZYNSKA =
            WeatherStationModel(
                23,
                Type.UMCS_TWO,
                283,
                423,
                51.442264,
                23.129692,
                "Wola Wereszczyńska"
            )
        val CELEJOW =
            WeatherStationModel(24, Type.UMCS_TWO, 269, 430, 51.330653, 22.071947, "Celejów")

        private val umcsStations = listOf(
            PLAC_LITEWSKI,
            OGROD_BOTANICZNY,
            ZEMBORZYCKA,
            HAJDOW,
            LUBARTOW,
            GUCIOW,
//                FLORIANKA,
            LUKOW,
            TRZDNIK,
            LESNIOWICE,
            RYBCZEWICE,
            WOLA_WERESZCZYNSKA,
            CELEJOW
        )

        // IMGW weather stations https://danepubliczne.imgw.pl/api/data/synop
        val TERESPOL_SIMPLE =
            WeatherStationModel(12399, Type.IMGW_SIMPLE, 276, 416, 52.078611, 23.621944, "Terespol")
        val WLODAWA_SIMPLE =
            WeatherStationModel(
                12497, Type.IMGW_SIMPLE, 276, 416, 51.553333,
                23.529444, "Włodawa"
            )
        val LUBLIN_SIMPLE =
            WeatherStationModel(
                12495, Type.IMGW_SIMPLE, 276, 416, 51.216666,
                22.393055, "LUBLIN"
            )
        val ZAMOSC_SIMPLE =
            WeatherStationModel(
                12595, Type.IMGW_SIMPLE, 276, 416, 50.698333,
                23.205555, "Zamość"
            )

        private val imgwStations = listOf(
            TERESPOL_SIMPLE, WLODAWA_SIMPLE, LUBLIN_SIMPLE, ZAMOSC_SIMPLE
        )


        //POGODYNKA weather stations http://monitor.pogodynka.pl/api/map/?category=meteo
        val TERESPOL =
            WeatherStationModel(
                352230399,
                Type.IMGW_POGODYNKA,
                297,
                409,
                52.078611,
                23.621944,
                "Terespol"
            )
        val CICIBOR =
            WeatherStationModel(
                252230190,
                Type.IMGW_POGODYNKA,
                283,
                409,
                52.076944,
                23.109444,
                "Cicibór"
            )
        val JARCZEW =
            WeatherStationModel(
                251210040,
                Type.IMGW_POGODYNKA,
                269,
                416,
                51.814722,
                21.973333,
                "Jarczew"
            )
        val RADZYN_PODLASKI =
            WeatherStationModel(
                251220040,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                51.769166,
                22.600833,
                "Radzyń Podlaski"
            )
        val WLODAWA =
            WeatherStationModel(
                351230497,
                Type.IMGW_POGODYNKA,
                290,
                423,
                51.553333,
                23.529444,
                "Włodawa"
            )
        val PULAWY =
            WeatherStationModel(
                251210120,
                Type.IMGW_POGODYNKA,
                269,
                430,
                51.412777,
                21.966111,
                "Puławy"
            )
        val RADAWIEC =
            WeatherStationModel(
                351220495,
                Type.IMGW_POGODYNKA,
                276,
                430,
                51.216666,
                22.393055,
                "Radawiec"
            )
        val BEZEK =
            WeatherStationModel(
                251230120,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                51.177222,
                23.263611,
                "Bezek"
            )
        val ANNOPOL =
            WeatherStationModel(
                250210030,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                50.889444,
                21.834166,
                "Annopol"
            )
        val WYSOKIE =
            WeatherStationModel(
                250220030,
                Type.IMGW_POGODYNKA,
                276,
                444,
                50.917777,
                22.666944,
                "Wysokie"
            )
        val STRZYZOW =
            WeatherStationModel(
                250240010,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                50.84,
                24.035555,
                "Strzyżów"
            )
        val NIELISZ =
            WeatherStationModel(
                250230020,
                Type.IMGW_POGODYNKA,
                283,
                444,
                50.805,
                23.038611,
                "Nielisz"
            )
        val ZAKLODZIE =
            WeatherStationModel(
                250220050,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                50.7575,
                22.861388,
                "Zakłodzie"
            )
        val ZAMOSC =
            WeatherStationModel(
                350230595,
                Type.IMGW_POGODYNKA,
                290,
                444,
                50.698333,
                23.205555,
                "Zamość"
            )
        val FRAMPOL =
            WeatherStationModel(
                250220080,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                50.675,
                22.666111,
                "Frampol"
            )
        val BRODZIAKI =
            WeatherStationModel(
                250220130,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                50.5066,
                22.790,
                "Brodziaki"
            )
        val JOZEFOW =
            WeatherStationModel(
                250230060,
                Type.IMGW_POGODYNKA,
                276, // bad
                416, // bad
                50.4875,
                23.05833,
                "Józefów"
            )
        val TOMASZOW_LUBELSKI =
            WeatherStationModel(
                250230070,
                Type.IMGW_POGODYNKA,
                290,
                451,
                50.45805,
                23.3988,
                "Tomaszów Lubelski"
            )
        val TARNOGROD =
            WeatherStationModel(
                250220140,
                Type.IMGW_POGODYNKA,
                283,
                458,
                50.356,
                22.756,
                "Tarnogród"
            )

        // commented out stations without temperature
        private val imgwPogodynkaStations = listOf(
            TERESPOL,
            CICIBOR,
            JARCZEW,
//            RADZYN_PODLASKI,
            WLODAWA,
            PULAWY,
            RADAWIEC,
//            BEZEK,
//            ANNOPOL,
            WYSOKIE,
//            STRZYZOW,
            NIELISZ,
//            ZAKLODZIE,
            ZAMOSC,
//            FRAMPOL,
//            BRODZIAKI,
//            JOZEFOW,
            TOMASZOW_LUBELSKI,
            TARNOGROD
        )

        fun getStations(): List<WeatherStationModel> {
            return umcsStations + imgwPogodynkaStations
        }

        fun getStationForGivenId(id: Int): WeatherStationModel {
            return getStations().single { it.stationId == id }
        }

        fun windIntToDir(windDirInt: Double?, returnEmpty: Boolean = false): Int = when {
            windDirInt == null -> if (returnEmpty) android.R.id.empty else R.drawable.ic_wind
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

    enum class Type {
        UMCS_ONE, UMCS_TWO, IMGW_SIMPLE, IMGW_POGODYNKA
    }
}