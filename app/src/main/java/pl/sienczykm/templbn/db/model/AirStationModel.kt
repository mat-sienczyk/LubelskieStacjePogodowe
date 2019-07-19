package pl.sienczykm.templbn.db.model


import androidx.room.Entity
import androidx.room.Ignore
import pl.sienczykm.templbn.utils.round

@Entity
class AirStationModel constructor(
    @Ignore
    override val stationId: Int,
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
        return "http://powietrze.gios.gov.pl/pjp/current/station_details/chart/$stationId"
    }

    init {
        this.url = getStationUrl()
    }

    var sensors: List<AirSensorModel>? = null

    override fun copy(): AirStationModel {
        val stationCopy =
            AirStationModel(stationId, latitude, longitude, city, location)
        stationCopy.url = url
        stationCopy.favorite = favorite
        stationCopy.date = date
        stationCopy.distance = distance

        stationCopy.sensors = sensors
        return stationCopy
    }

    override fun isContentTheSame(other: BaseStationModel?): Boolean {
        if (super.isContentTheSame(other)) {
            other as AirStationModel
            if (sensors != other.sensors) return false
        }

        return super.isContentTheSame(other)
    }

    fun getValue(sensorType: AirSensorType): Double? {
        return sensors?.find { airSensorModel -> airSensorModel.paramCode == sensorType.paramKey }
            ?.data?.lastOrNull { chartDataModel -> chartDataModel.value != null }
            ?.value.round(1)
    }

    fun getChartDataForSensor(sensorType: AirSensorType): List<ChartDataModel>? {
        return sensors?.find { airSensorModel -> airSensorModel.paramCode == sensorType.paramKey }
            ?.data?.filter { it.value != null }
    }

    fun getValueAndQualityIndex(sensorType: AirSensorType): Pair<Double, AirQualityIndex>? {
        val value = getValue(sensorType) ?: return null
        return when {
            (value > 0) and (value <= sensorType.maxVeryGood) -> value to AirQualityIndex.VERY_GOOD
            (value > sensorType.maxVeryGood) and (value <= sensorType.maxGood) -> value to AirQualityIndex.GOOD
            (value > sensorType.maxGood) and (value <= sensorType.maxModerate) -> value to AirQualityIndex.MODERATE
            (value > sensorType.maxModerate) and (value <= sensorType.maxUnhealthySensitive) -> value to AirQualityIndex.UNHEALTHY_SENSITIVE
            (value > sensorType.maxUnhealthySensitive) and (value <= sensorType.maxUnhealthy) -> value to AirQualityIndex.UNHEALTHY
            value > sensorType.maxUnhealthy -> value to AirQualityIndex.HAZARDOUS
            else -> null
        }
    }

    companion object {

        val ID_KEY = "air_station_id"

        val LUBLIN = AirStationModel(266, 51.259431, 22.569133, "Lublin", "ul. Obywatelska")
        val BIALA_PODLASKA =
            AirStationModel(236, 52.029194, 23.149389, "Biała Podlaska", "ul. Orzechowa")
        val JARCZEW = AirStationModel(248, 51.814367, 21.972375, "Jarczew")
        val WILCZOPOLE = AirStationModel(282, 51.163542, 22.598608, "Wilczopole")
        val ZAMOSC = AirStationModel(285, 50.716628, 23.290247, "Zamość", "ul. Hrubieszowska 69A")
        val PULAWY = AirStationModel(9593, 51.419047, 21.961089, "Puławy", "ul. Karpińskiego")
        val FLORAINKA = AirStationModel(10874, 50.551894, 22.982861, "Florianka")
        val CHELM = AirStationModel(11360, 51.122190, 23.472870, "Chełm", "ul. Połaniecka")
        val NALECZOW = AirStationModel(11362, 51.284931, 22.210242, "Nałęczów")

        fun getStations(): List<AirStationModel> {
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

        fun getStationForGivenId(id: Int): AirStationModel {
            return getStations().single { it.stationId == id }
        }
    }

    enum class AirSensorType(
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

    enum class AirQualityIndex(val value: Int, val desc: String) {
        VERY_GOOD(0, "Bardzo dobry"),
        GOOD(1, "Dobry"),
        MODERATE(2, "Umiarkowany"),
        UNHEALTHY_SENSITIVE(3, "Dostateczny"),
        UNHEALTHY(4, "Zły"),
        HAZARDOUS(5, "Bardzo zły"),
    }
}