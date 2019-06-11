package pl.sienczykm.templbn.utils

class SmogStation private constructor(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    val url: String
//    var distance: Double = 0.toDouble()

    init {
        this.url = getStationUrl()
    }

    override fun toString(): String {
        return name
    }

    private fun getStationUrl(): String {
        return "http://powietrze.gios.gov.pl/pjp/current/station_details/chart/$id"
    }

    companion object {

        val STATION_ID_KEY = "smog_station_id"

        val LUBLIN = SmogStation(266, "Lublin", 51.259431, 22.569133)
        val BIALA_PODLASKA = SmogStation(236, "Biała Podlaska", 52.029194, 23.149389)
        val JARCZEW = SmogStation(248, "Jarczew", 51.814367, 21.972375)
        val WILCZOPOLE = SmogStation(282, "Wilczopole", 51.163542, 22.598608)
        val ZAMOSC = SmogStation(285, "Zamość", 50.716628, 23.290247)
        val PULAWY = SmogStation(9593, "Puławy", 51.419047, 21.961089)
        val FLORAINKA = SmogStation(10874, "Florianka", 50.551894, 22.982861)
        val CHELM = SmogStation(11360, "Chełm", 51.122190, 23.472870)
        val NALECZOW = SmogStation(11362, "Nałęczów", 51.284931, 22.210242)

        val STATIONS = arrayOf(
            LUBLIN,
            BIALA_PODLASKA,
            JARCZEW,
            WILCZOPOLE,
            ZAMOSC,
            PULAWY,
            FLORAINKA,
            CHELM,
            NALECZOW
        )

//        fun getStationForGivenId(id: Int): SmogStation {
//
//            var selectedStation = LUBLIN
//
//            for (station in STATIONS) {
//                if (station.id == id) {
//                    selectedStation = station
//                }
//            }
//
//            return selectedStation
//        }
    }
}