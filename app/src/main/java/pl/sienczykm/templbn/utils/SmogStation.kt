package pl.sienczykm.templbn.utils

class SmogStation private constructor(id: Int, name: String, latitude: Double, longitude: Double) : Station(id, name, latitude, longitude) {

    init {
        this.url = getStationUrl()
    }

    override fun getStationUrl(): String {
        return "http://powietrze.gios.gov.pl/pjp/current/station_details/chart/$id"
    }

    companion object {

        val ID_KEY = "smog_station_id"

        val LUBLIN = SmogStation(266, "Lublin", 51.259431, 22.569133)
        val BIALA_PODLASKA = SmogStation(236, "Biała Podlaska", 52.029194, 23.149389)
        val JARCZEW = SmogStation(248, "Jarczew", 51.814367, 21.972375)
        val WILCZOPOLE = SmogStation(282, "Wilczopole", 51.163542, 22.598608)
        val ZAMOSC = SmogStation(285, "Zamość", 50.716628, 23.290247)
        val PULAWY = SmogStation(9593, "Puławy", 51.419047, 21.961089)
        val FLORAINKA = SmogStation(10874, "Florianka", 50.551894, 22.982861)
        val CHELM = SmogStation(11360, "Chełm", 51.122190, 23.472870)
        val NALECZOW = SmogStation(11362, "Nałęczów", 51.284931, 22.210242)

        fun getStations(): List<SmogStation> {
            return listOf(
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
        }

        fun getStationForGivenId(id: Int): SmogStation {
            return getStations().single { it.id == id }
        }

    }
}