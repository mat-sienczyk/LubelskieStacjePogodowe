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

    fun getPM25(): String? {
        return getData(SmogSensorType.PM25)
    }

    fun getPM10(): String? {
        return getData(SmogSensorType.PM10)
    }

    fun getCO(): String? {
        return getData(SmogSensorType.CO)
    }

    fun getNO2(): String? {
        return getData(SmogSensorType.NO2)
    }

    fun getO3(): String? {
        return getData(SmogSensorType.O3)
    }

    fun getSO2(): String? {
        return getData(SmogSensorType.SO2)
    }

    fun getC6H6(): String? {
        return getData(SmogSensorType.C6H6)
    }

    private fun getData(sensorType: SmogSensorType): String? {
        return sensors?.find { smogSensorModel -> smogSensorModel.paramCode == sensorType.paramKey }
            ?.data?.first { chartDataModel -> chartDataModel.value != null }
            ?.value?.toString()
    }

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

    enum class SmogSensorType(val paramName: String, val paramKey: String, val paramId: Int) {
        CO("tlenek węgla", "CO", 8),
        NO2("dwutlenek azotu", "NO2", 6),
        O3("ozon", "O3", 5),
        PM10("pył zawieszony PM10", "PM10", 3),
        SO2("dwutlenek siarki", "SO2", 1),
        C6H6("benzen", "C6H6", 10),
        PM25("pył zawieszony PM2.5", "PM2.5", 69)
    }
}