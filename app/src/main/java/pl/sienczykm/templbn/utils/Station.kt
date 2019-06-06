package pl.sienczykm.templbn.utils

class Station private constructor(
    val id: Int,
    val parser: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    val jsonUrl: String
    val url: String
    var distance: Double = 0.toDouble()

    init {
        this.jsonUrl = constructJsonUrl(parser, id)
        this.url = getStationUrl(jsonUrl)
    }

    override fun toString(): String {
        return name
    }

    private fun getStationUrl(url: String): String {
        return Config.BASE_URL + "podglad/" + url.substring(url.length - 2, url.length)
    }

    private fun constructJsonUrl(parser: Int, id: Int): String {

        var jsonUrl = Config.BASE_URL

        if (parser == PARSER_I) {
            jsonUrl = jsonUrl + "data.php?s="
        } else {
            jsonUrl = jsonUrl + "data2.php?s="
        }

        jsonUrl = jsonUrl + id

        return jsonUrl
    }

    companion object {

        val STATION_ID_KEY = "station_id"

        val PARSER_I = 1
        val PARSER_II = 2

        val OGROD_BOTANICZNY = Station(10, PARSER_I, "Ogród botaniczny", 51.263975, 22.514608)
        val GUCIOW = Station(11, PARSER_I, "Guciów", 50.582600, 23.073628)
        val FLORIANKA = Station(12, PARSER_II, "Florianka", 50.554803, 22.988150)
        val LUKOW = Station(13, PARSER_II, "Łuków", 51.930883, 22.389122)
        val PLAC_LITEWSKI = Station(16, PARSER_I, "Plac Litewski", 51.248831, 22.560531)
        val ZEMBORZYCKA = Station(17, PARSER_I, "MPWiK Zemborzycka", 51.203525, 22.561972)
        val HAJDOW = Station(18, PARSER_II, "MPWiK Hajdów", 51.264328, 22.622867)
        val LUBARTOW = Station(19, PARSER_I, "PGK Lubartów", 51.452850, 22.590253)
        val TRZDNIK = Station(20, PARSER_II, "Trzydnik", 50.851986, 22.134056)
        val LESNIOWICE = Station(21, PARSER_II, "Leśniowice", 50.988278, 23.509881)
        val RYBCZEWICE = Station(22, PARSER_II, "Rybczewice", 51.039969, 22.853811)
        val WOLA_WERESZCZYNSKA = Station(23, PARSER_II, "Wola Wereszczyńska", 51.442264, 23.129692)
        val CELEJOW = Station(24, PARSER_II, "Celejów", 51.330653, 22.071947)

        val STATIONS = arrayOf(
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

        fun getStationForGivenId(id: Int): Station {

            var selectedStation = PLAC_LITEWSKI

            for (station in STATIONS) {
                if (station.id == id) {
                    selectedStation = station
                }
            }

            return selectedStation
        }
    }
}