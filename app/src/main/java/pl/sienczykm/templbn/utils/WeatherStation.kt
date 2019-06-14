package pl.sienczykm.templbn.utils

class WeatherStation private constructor(id: Int, type: Type, name: String, latitude: Double, longitude: Double) :
    Station(id, name, latitude, longitude) {

    var type: Type

    init {
        this.url = getStationUrl()
        this.type = type
    }

    override fun getStationUrl(): String {
        return Config.BASE_WEATHER_URL + "podglad/" + id
    }

    companion object {

        val ID_KEY = "weather_station_id"

        val OGROD_BOTANICZNY = WeatherStation(10, Type.ONE, "Ogród botaniczny", 51.263975, 22.514608)
        val GUCIOW = WeatherStation(11, Type.ONE, "Guciów", 50.582600, 23.073628)
        val FLORIANKA = WeatherStation(12, Type.TWO, "Florianka", 50.554803, 22.988150)
        val LUKOW = WeatherStation(13, Type.TWO, "Łuków", 51.930883, 22.389122)
        val PLAC_LITEWSKI = WeatherStation(16, Type.ONE, "Plac Litewski", 51.248831, 22.560531)
        val ZEMBORZYCKA = WeatherStation(17, Type.ONE, "MPWiK Zemborzycka", 51.203525, 22.561972)
        val HAJDOW = WeatherStation(18, Type.TWO, "MPWiK Hajdów", 51.264328, 22.622867)
        val LUBARTOW = WeatherStation(19, Type.ONE, "PGK Lubartów", 51.452850, 22.590253)
        val TRZDNIK = WeatherStation(20, Type.TWO, "Trzydnik", 50.851986, 22.134056)
        val LESNIOWICE = WeatherStation(21, Type.TWO, "Leśniowice", 50.988278, 23.509881)
        val RYBCZEWICE = WeatherStation(22, Type.TWO, "Rybczewice", 51.039969, 22.853811)
        val WOLA_WERESZCZYNSKA = WeatherStation(23, Type.TWO, "Wola Wereszczyńska", 51.442264, 23.129692)
        val CELEJOW = WeatherStation(24, Type.TWO, "Celejów", 51.330653, 22.071947)

        fun getStations(): List<WeatherStation> {
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

        fun getStationForGivenId(id: Int): WeatherStation {
            return getStations().single { it.id == id }
        }
    }

    enum class Type {
        ONE, TWO
    }
}