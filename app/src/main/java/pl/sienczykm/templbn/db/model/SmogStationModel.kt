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
    StationModel(stationId, name, latitude, longitude) {

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

    fun getPM10(): String? {
        return getValue(SmogSensorType.PM10)?.toString()
    }

    fun getCO(): String? {
        return getValue(SmogSensorType.CO)?.toString()
    }

    fun getNO2(): String? {
        return getValue(SmogSensorType.NO2)?.toString()
    }

    fun getO3(): String? {
        return getValue(SmogSensorType.O3)?.toString()
    }

    fun getSO2(): String? {
        return getValue(SmogSensorType.SO2)?.toString()
    }

    fun getC6H6(): String? {
        return getValue(SmogSensorType.C6H6)?.toString()
    }

    fun getQualityIndex(sensorType: SmogSensorType): QualityIndex? {
        val value = getValue(sensorType) ?: return null
        when (sensorType) {
            SmogSensorType.PM10 -> {
                return when {
                    (value > 0) and (value <= 21) -> QualityIndex.VERY_GOOD
                    (value > 21) and (value <= 61) -> QualityIndex.GOOD
                    (value > 61) and (value <= 101) -> QualityIndex.MODERATE
                    (value > 101) and (value <= 141) -> QualityIndex.UNHEALTHY_SENSITIVE
                    (value > 141) and (value <= 201) -> QualityIndex.UNHEALTHY
                    value > 201 -> QualityIndex.HAZARDOUS
                    else -> null
                }
            }
            SmogSensorType.PM25 -> {
                return when {
                    (value > 0) and (value <= 13) -> QualityIndex.VERY_GOOD
                    (value > 13) and (value <= 37) -> QualityIndex.GOOD
                    (value > 37) and (value <= 61) -> QualityIndex.MODERATE
                    (value > 61) and (value <= 85) -> QualityIndex.UNHEALTHY_SENSITIVE
                    (value > 85) and (value <= 121) -> QualityIndex.UNHEALTHY
                    value > 121 -> QualityIndex.HAZARDOUS
                    else -> null
                }
            }
            SmogSensorType.O3 -> {
                return when {
                    (value > 0) and (value <= 71) -> QualityIndex.VERY_GOOD
                    (value > 71) and (value <= 121) -> QualityIndex.GOOD
                    (value > 121) and (value <= 151) -> QualityIndex.MODERATE
                    (value > 151) and (value <= 181) -> QualityIndex.UNHEALTHY_SENSITIVE
                    (value > 181) and (value <= 241) -> QualityIndex.UNHEALTHY
                    value > 241 -> QualityIndex.HAZARDOUS
                    else -> null
                }
            }
            SmogSensorType.NO2 -> {
                return when {
                    (value > 0) and (value <= 41) -> QualityIndex.VERY_GOOD
                    (value > 41) and (value <= 101) -> QualityIndex.GOOD
                    (value > 101) and (value <= 151) -> QualityIndex.MODERATE
                    (value > 151) and (value <= 201) -> QualityIndex.UNHEALTHY_SENSITIVE
                    (value > 201) and (value <= 401) -> QualityIndex.UNHEALTHY
                    value > 401 -> QualityIndex.HAZARDOUS
                    else -> null
                }
            }
            SmogSensorType.SO2 -> {
                return when {
                    (value > 0) and (value <= 51) -> QualityIndex.VERY_GOOD
                    (value > 51) and (value <= 101) -> QualityIndex.GOOD
                    (value > 101) and (value <= 201) -> QualityIndex.MODERATE
                    (value > 201) and (value <= 351) -> QualityIndex.UNHEALTHY_SENSITIVE
                    (value > 351) and (value <= 501) -> QualityIndex.UNHEALTHY
                    value > 501 -> QualityIndex.HAZARDOUS
                    else -> null
                }
            }
            SmogSensorType.C6H6 -> {
                return when {
                    (value > 0) and (value <= 6) -> QualityIndex.VERY_GOOD
                    (value > 6) and (value <= 11) -> QualityIndex.GOOD
                    (value > 11) and (value <= 16) -> QualityIndex.MODERATE
                    (value > 16) and (value <= 21) -> QualityIndex.UNHEALTHY_SENSITIVE
                    (value > 21) and (value <= 51) -> QualityIndex.UNHEALTHY
                    value > 51 -> QualityIndex.HAZARDOUS
                    else -> null
                }
            }
            SmogSensorType.CO -> {
                return when {
                    (value > 0) and (value <= 3) -> QualityIndex.VERY_GOOD
                    (value > 3) and (value <= 7) -> QualityIndex.GOOD
                    (value > 7) and (value <= 11) -> QualityIndex.MODERATE
                    (value > 11) and (value <= 15) -> QualityIndex.UNHEALTHY_SENSITIVE
                    (value > 15) and (value <= 21) -> QualityIndex.UNHEALTHY
                    value > 21 -> QualityIndex.HAZARDOUS
                    else -> null
                }
            }
        }
    }

    private fun getValue(sensorType: SmogSensorType): Double? {
        return sensors?.find { smogSensorModel -> smogSensorModel.paramCode == sensorType.paramKey }
            ?.data?.first { chartDataModel -> chartDataModel.value != null }
            ?.value.round(1)
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

    enum class QualityIndex(val value: Int, val desc: String) {
        VERY_GOOD(0, "Bardzo dobry"),
        GOOD(1, "Dobry"),
        MODERATE(2, "Umiarkowany"),
        UNHEALTHY_SENSITIVE(3, "Dostateczny"),
        UNHEALTHY(4, "Zły"),
        HAZARDOUS(5, "Bardzo zły"),
    }
}