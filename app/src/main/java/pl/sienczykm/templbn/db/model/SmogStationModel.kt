package pl.sienczykm.templbn.db.model


import androidx.room.Entity
import androidx.room.Ignore
import pl.sienczykm.templbn.utils.round

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
    BaseStationModel(stationId, name, latitude, longitude) {

    override fun getStationUrl(): String {
        return "http://powietrze.gios.gov.pl/pjp/current/station_details/chart/$stationId"
    }

    init {
        this.url = getStationUrl()
    }

    var sensors: List<SmogSensorModel>? = null

    fun getPM25(): String? {
        return getValue(SmogSensorType.PM25)?.toString()
    }

    fun getValue(sensorType: SmogSensorType): Double? {
        return sensors?.find { smogSensorModel -> smogSensorModel.paramCode == sensorType.paramKey }
            ?.data?.first { chartDataModel -> chartDataModel.value != null }
            ?.value.round(1)
    }

    fun getValueAndQualityIndex(sensorType: SmogSensorType): Pair<Double, QualityIndex>? {
        val value = getValue(sensorType) ?: return null
        return when {
            (value > 0) and (value <= sensorType.maxVeryGood) -> value to QualityIndex.VERY_GOOD
            (value > sensorType.maxVeryGood) and (value <= sensorType.maxGood) -> value to QualityIndex.GOOD
            (value > sensorType.maxGood) and (value <= sensorType.maxModerate) -> value to QualityIndex.MODERATE
            (value > sensorType.maxModerate) and (value <= sensorType.maxUnhealthySensitive) -> value to QualityIndex.UNHEALTHY_SENSITIVE
            (value > sensorType.maxUnhealthySensitive) and (value <= sensorType.maxUnhealthy) -> value to QualityIndex.UNHEALTHY
            value > sensorType.maxUnhealthy -> value to QualityIndex.HAZARDOUS
            else -> null
        }
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

    enum class SmogSensorType(
        val paramName: String,
        val paramKey: String,
        val paramId: Int,
        val maxVeryGood: Int,
        val maxGood: Int,
        val maxModerate: Int,
        val maxUnhealthySensitive: Int,
        val maxUnhealthy: Int
    ) {
        PM10("pył zawieszony PM10", "PM10", 3, 21, 61, 101, 141, 201),
        PM25("pył zawieszony PM2.5", "PM2.5", 69, 13, 37, 61, 85, 121),
        O3("ozon", "O3", 5, 71, 121, 151, 181, 241),
        NO2("dwutlenek azotu", "NO2", 6, 41, 101, 151, 201, 401),
        SO2("dwutlenek siarki", "SO2", 1, 51, 101, 201, 351, 501),
        C6H6("benzen", "C6H6", 10, 6, 11, 16, 21, 51),
        CO("tlenek węgla", "CO", 8, 3, 7, 11, 15, 21)
    }

    enum class QualityIndex(val value: Int, val desc: String) {
        VERY_GOOD(0, "Bardzo dobry"),
        GOOD(1, "Dobry"),
        MODERATE(2, "Umiarkowany"),
        UNHEALTHY_SENSITIVE(3, "Dostateczny"),
        UNHEALTHY(4, "Zły"),
        HAZARDOUS(5, "Bardzo zły"),
    }
}