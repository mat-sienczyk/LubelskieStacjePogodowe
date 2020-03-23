package pl.sienczykm.templbn.db.model


import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.Ignore
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.utils.nowInPoland
import pl.sienczykm.templbn.utils.round
import java.util.*

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

    override fun getOldDateTimeInMinutes(): Long = 180

    init {
        this.url = getStationUrl()
    }

    var sensors: List<AirSensorModel>? = null
    var airQualityIndex: Int? = null

    override fun copy(): AirStationModel {
        val stationCopy =
            AirStationModel(stationId, latitude, longitude, city, location)
        stationCopy.url = url
        stationCopy.favorite = favorite
        stationCopy.date = date
        stationCopy.distance = distance
        stationCopy.airQualityIndex = airQualityIndex
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

        val yesterday = nowInPoland().apply {
            add(Calendar.HOUR_OF_DAY, -1)
            add(Calendar.DAY_OF_MONTH, -1)
        }

        return sensors?.find { airSensorModel -> airSensorModel.paramCode == sensorType.paramKey }
            ?.data?.filter { (it.timestamp!! > yesterday.timeInMillis) and (it.value != null) }
    }

    fun getValueAndQualityIndex(sensorType: AirSensorType): Pair<Double, AirQualityIndex>? {
        val value = getValue(sensorType) ?: return null
        return when {
            (value >= 0) and (value <= sensorType.maxVeryGood) -> value to AirQualityIndex.VERY_GOOD
            (value > sensorType.maxVeryGood) and (value <= sensorType.maxGood) -> value to AirQualityIndex.GOOD
            (value > sensorType.maxGood) and (value <= sensorType.maxModerate) -> value to AirQualityIndex.MODERATE
            (value > sensorType.maxModerate) and (value <= sensorType.maxUnhealthySensitive) -> value to AirQualityIndex.UNHEALTHY_SENSITIVE
            (value > sensorType.maxUnhealthySensitive) and (value <= sensorType.maxUnhealthy) -> value to AirQualityIndex.UNHEALTHY
            value > sensorType.maxUnhealthy -> value to AirQualityIndex.HAZARDOUS
            else -> null
        }
    }

    fun getOverallAirQualityIndex(): AirQualityIndex? {
        return when (airQualityIndex) {
            0 -> AirQualityIndex.VERY_GOOD
            1 -> AirQualityIndex.GOOD
            2 -> AirQualityIndex.MODERATE
            3 -> AirQualityIndex.UNHEALTHY_SENSITIVE
            4 -> AirQualityIndex.UNHEALTHY
            5 -> AirQualityIndex.HAZARDOUS
            else -> null
        }
    }

    fun aboutAirIndexUrl(): String {
        return "https://powietrze.gios.gov.pl/pjp/content/show/1001197"
    }

    companion object {

        const val ID_KEY = "air_station_id"

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
        val KRASNOBROD =
            AirStationModel(12098, 50.549297, 23.197317, "Krasnobród", "ul. Sanatoryjna")

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
                NALECZOW,
                KRASNOBROD
            )
        }

        fun getStationForGivenId(id: Int): AirStationModel {
            return getStations().single { it.stationId == id }
        }
    }

    enum class AirSensorType(
        @StringRes val paramName: Int,
        val paramKey: String,
        val paramId: Int,
        val maxVeryGood: Int,
        val maxGood: Int,
        val maxModerate: Int,
        val maxUnhealthySensitive: Int,
        val maxUnhealthy: Int
    ) {
        PM10(R.string.sensor_type_pm10, "PM10", 3, 21, 61, 101, 141, 201),
        PM25(R.string.sensor_type_pm25, "PM2.5", 69, 13, 37, 61, 85, 121),
        O3(R.string.sensor_type_o3, "O3", 5, 71, 121, 151, 181, 241),
        NO2(R.string.sensor_type_no2, "NO2", 6, 41, 101, 151, 201, 401),
        SO2(R.string.sensor_type_so2, "SO2", 1, 51, 101, 201, 351, 501),
        C6H6(R.string.sensor_type_c6h6, "C6H6", 10, 6, 11, 16, 21, 51),
        CO(R.string.sensor_type_co, "CO", 8, 3, 7, 11, 15, 21)
    }

    enum class AirQualityIndex(val value: Int, @StringRes val description: Int, @ColorRes val color: Int) {
        VERY_GOOD(0, R.string.sensor_quality_very_good, R.color.quality_very_good),
        GOOD(1, R.string.sensor_quality_good, R.color.quality_good),
        MODERATE(2, R.string.sensor_quality_moderate, R.color.quality_moderate),
        UNHEALTHY_SENSITIVE(3, R.string.sensor_quality_unhealthy_sensitive, R.color.quality_unhealthy_sensitive),
        UNHEALTHY(4, R.string.sensor_quality_unhealthy, R.color.quality_unhealthy),
        HAZARDOUS(5, R.string.sensor_quality_hazardous, R.color.quality_hazardous),
    }
}