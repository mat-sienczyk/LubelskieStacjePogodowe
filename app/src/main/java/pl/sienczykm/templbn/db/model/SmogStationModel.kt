package pl.sienczykm.templbn.db.model


import androidx.room.Entity
import androidx.room.Ignore

@Entity
data class SmogStationModel constructor(
    @Ignore
    override val stationId: Int,
    @Ignore
    override val name: String,
    @Ignore
    override val latitude: Double,
    @Ignore
    override val longitude: Double
) :
    StationModel(stationId, name, latitude, longitude) {

    override fun getStationUrl(): String {
        return "http://powietrze.gios.gov.pl/pjp/current/station_details/chart/$stationId"
    }

    init {
        this.url = getStationUrl()
    }

    var sensors: List<SmogSensorModel>? = null

    companion object {

        val ID_KEY = "smog_station_id"

        val LUBLIN = SmogStationModel(266, "Lublin", 51.259431, 22.569133)
        val BIALA_PODLASKA = SmogStationModel(236, "Biała Podlaska", 52.029194, 23.149389)
        val JARCZEW = SmogStationModel(248, "Jarczew", 51.814367, 21.972375)
        val WILCZOPOLE = SmogStationModel(282, "Wilczopole", 51.163542, 22.598608)
        val ZAMOSC = SmogStationModel(285, "Zamość", 50.716628, 23.290247)
        val PULAWY = SmogStationModel(9593, "Puławy", 51.419047, 21.961089)
        val FLORAINKA = SmogStationModel(10874, "Florianka", 50.551894, 22.982861)
        val CHELM = SmogStationModel(11360, "Chełm", 51.122190, 23.472870)
        val NALECZOW = SmogStationModel(11362, "Nałęczów", 51.284931, 22.210242)

        fun getStations(): List<SmogStationModel> {
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

        fun getStationForGivenId(id: Int): SmogStationModel {
            return getStations().single { it.stationId == id }
        }
    }
}