package pl.sienczykm.templbn.background

import android.content.Context
import androidx.annotation.WorkerThread
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.DataModelDb
import pl.sienczykm.templbn.db.model.SmogSensorDb
import pl.sienczykm.templbn.db.model.SmogStationDb
import pl.sienczykm.templbn.db.model.WeatherStationDb
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.remote.model.SmogSensorData
import pl.sienczykm.templbn.utils.Constants
import pl.sienczykm.templbn.utils.WeatherStation
import java.text.SimpleDateFormat
import java.util.*

object ProcessingUtils {

    const val ERROR_KEY = "error_key"

    @WorkerThread
    fun updateSmogStation(appContext: Context, stationId: Int) {

        AppDb.getDatabase(appContext).smogStationDao()
            .insert(SmogStationDb(stationId, getSensors(stationId)))

    }

    @WorkerThread
    fun updateWeatherStation(appContext: Context, stationId: Int) {

        AppDb.getDatabase(appContext).tempStationDao()
                .insert(
                    if (WeatherStation.getStationForGivenId(stationId).type == WeatherStation.Type.ONE) {

                        val response = LspController.getWeatherStationOne(stationId)

                        when {
                            response.isSuccessful -> {
                                val responseStation = response.body()

                                WeatherStationDb(
                                    stationId,
                                    parseWeatherDate(responseStation?.data),
                                    responseStation?.temperature,
                                    responseStation?.temperatureWindChill,
                                    null,
                                    responseStation?.windSpeed,
                                    responseStation?.windDir,
                                    responseStation?.humidity,
                                    responseStation?.pressure,
                                    responseStation?.rainToday,
                                    parseChartData(responseStation?.temperatureData?.data),
                                    parseChartData(responseStation?.humidityData?.data),
                                    parseChartData(responseStation?.windSpeedData?.data),
                                    parseChartData(responseStation?.temperatureWindChart?.data),
                                    parseChartData(responseStation?.pressureData?.data, true),
                                    parseChartData(responseStation?.rainData?.data)
                                )
                            }
                            else -> throw Exception(response.errorBody().toString())
                        }

                    } else {

                        val response = LspController.getWeatherStationTwo(stationId)

                        when {
                            response.isSuccessful -> {
                                val responseStation = response.body()

                                WeatherStationDb(
                                    stationId,
                                    parseWeatherDate(responseStation?.data),
                                    responseStation?.temperature,
                                    null,
                                    responseStation?.temperatureGround,
                                    responseStation?.windSpeed,
                                    responseStation?.windDir,
                                    responseStation?.humidity,
                                    null,
                                    responseStation?.rainToday,
                                    parseChartData(responseStation?.temperatureData?.data),
                                    parseChartData(responseStation?.humidityData?.data)
                                )

                            }
                            else -> throw Exception(response.errorBody().toString())
                        }
                    }
                )
    }

    private fun parseWeatherDate(stringDate: String?): Date? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        return inputFormat.parse(stringDate)
    }

    private fun parseChartData(chartData: List<List<Double>?>?): List<DataModelDb>? {
        return parseChartData(chartData, false)
    }

    private fun parseChartData(chartData: List<List<Double>?>?, isPressure: Boolean): List<DataModelDb>? {
        val isDaylightTime = TimeZone.getTimeZone("Europe/Warsaw").inDaylightTime(Date())
        val offset = if (isDaylightTime) Constants.TWO_HOURS else Constants.ONE_HOUR

        return if (chartData.isNullOrEmpty()) null
        else {
            var returnList = chartData.filterNotNull()
            if (isPressure) returnList = returnList.filter { it[1] > 0 }
            returnList.map { DataModelDb(it[0].toLong().plus(offset), it[1]) }
        }
    }

    private fun getSensors(stationId: Int): List<SmogSensorDb>? {

        val response = LspController.getSmogSensors(stationId)

        return when {
            response.isSuccessful -> response.body()
                ?.filter { smogSensor -> smogSensor.id != null }
                ?.map { smogSensor ->
                    SmogSensorDb(
                        smogSensor.id,
                        smogSensor.param?.paramName,
                        smogSensor.param?.paramCode,
                        parseSensorData(LspController.getSmogSensorData(smogSensor.id!!).body())
                    )
                }
            else -> throw Exception(response.errorBody().toString())
        }
    }

    private fun parseSensorData(smogSensorData: SmogSensorData?): List<DataModelDb>? {
        return smogSensorData?.values?.map { DataModelDb(parseSmogDate(it.date), it.value) }
    }

    private fun parseSmogDate(stringDate: String?): Long? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return inputFormat.parse(stringDate).time
    }
}