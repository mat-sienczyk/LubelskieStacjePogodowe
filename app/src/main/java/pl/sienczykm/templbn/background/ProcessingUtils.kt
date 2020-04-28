package pl.sienczykm.templbn.background

import android.content.Context
import androidx.annotation.WorkerThread
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirSensorModel
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.dateFormat
import pl.sienczykm.templbn.utils.nowInPoland
import pl.sienczykm.templbn.utils.round
import pl.sienczykm.templbn.webservice.LspController
import pl.sienczykm.templbn.webservice.model.air.AirSensorData
import java.util.*
import java.util.concurrent.TimeUnit

object ProcessingUtils {

    const val ERROR_KEY = "error_key"

    @WorkerThread
    fun updateAirStation(appContext: Context, stationId: Int) {
        AppDb.getDatabase(appContext).airStationDao().apply {
            getStationById(stationId)?.let { insert(constructAirStationModel(it)) }
                ?: insert(constructAirStationModel(AirStationModel.getStationForGivenId(stationId)))
        }

    }

    @WorkerThread
    fun updateWeatherStation(appContext: Context, stationId: Int) {
        AppDb.getDatabase(appContext).weatherStationDao().apply {
            getStationById(stationId)?.let { insert(constructWeatherStationModel(it)) }
                ?: insert(
                    constructWeatherStationModel(WeatherStationModel.getStationForGivenId(stationId))
                )
        }
    }

    private fun constructAirStationModel(station: AirStationModel): AirStationModel {
        station.airQualityIndex = getOverallAirQualityIndex(station.stationId)
        station.sensors = getSensors(station.stationId)
        station.date = getAirStationDate(station.sensors)
        return station
    }

    private fun getAirStationDate(sensors: List<AirSensorModel>?): Date? {
        return sensors?.filter { sensorModel -> sensorModel.data?.isNotEmpty()!! }
            ?.maxBy { sensorModel -> sensorModel.data?.lastOrNull { dataModel -> dataModel.value != null }?.timestamp!! }
            ?.data?.lastOrNull { dataModel -> dataModel.value != null }?.timestamp?.let { Date(it) }
    }

    //TODO refactor this, create something more generic perhaps
    private fun constructWeatherStationModel(station: WeatherStationModel): WeatherStationModel {
        when (station.type) {
            WeatherStationModel.Type.UMCS_ONE -> LspController.getUmcsWeatherStationOne(station.stationId)
                .apply {
                    if (isSuccessful) {
                        body()?.apply {
                            station.date = parseUmcsWeatherDate(data)
                            station.temperature = temperature
                            station.temperatureWind = temperatureWindChill
                            station.windSpeed = windSpeed
                            station.windDir = windDir
                            station.humidity = humidity
                            station.pressure = pressure
                            station.rainToday = rainToday
                            station.temperatureData =
                                parseUmcsWeatherChartData(temperatureData?.data)
                            station.humidityData =
                                parseUmcsWeatherChartData(humidityData?.data)
                            station.windSpeedData =
                                parseUmcsWeatherChartData(windSpeedData?.data)
                            station.temperatureWindData =
                                parseUmcsWeatherChartData(temperatureWindChart?.data)
                            station.pressureData =
                                parseUmcsWeatherChartData(pressureData?.data, true)
                            station.rainTodayData =
                                parseUmcsWeatherChartData(rainData?.data)
                        }
                    } else throw Exception(errorBody().toString())
                }
            WeatherStationModel.Type.UMCS_TWO -> LspController.getUmcsWeatherStationTwo(station.stationId)
                .apply {
                    if (isSuccessful) {
                        body()?.apply {
                            station.date = parseUmcsWeatherDate(data)
                            station.temperature = temperature
                            station.temperatureGround = temperatureGround
                            station.windSpeed = windSpeed
                            station.windDir = windDir
                            station.humidity = humidity
                            station.rainToday = rainToday
                            station.temperatureWindData =
                                parseUmcsWeatherChartData(temperatureData?.data)
                            station.humidityData =
                                parseUmcsWeatherChartData(humidityData?.data)
                        }
                    } else throw Exception(errorBody().toString())
                }
            WeatherStationModel.Type.IMGW_POGODYNKA -> LspController.getPogodynkaWeatherStation(
                station.stationId
            )
                .apply {
                    if (isSuccessful) {
                        body()?.apply {
                            station.date =
                                parsePogodynkaWeatherDate(temperatureAutoRecords?.lastOrNull()?.date)
                            station.temperature = temperatureAutoRecords?.last()?.value
                        }
                    } else throw Exception(errorBody().toString())
                }
            else -> throw Exception("Unparsed ${station.type} type!")
        }

        return station
    }

    private fun parseUmcsWeatherDate(stringDate: String?): Date? =
        stringDate?.let { dateFormat("yyyy-MM-dd HH:mm", "UTC").parse(it) }

    private fun parsePogodynkaWeatherDate(stringDate: String?): Date? =
        stringDate?.let { dateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", "UTC").parse(it) }

    private fun parseUmcsWeatherChartData(
        chartData: List<List<Double>?>?,
        isPressure: Boolean = false
    ): List<ChartDataModel>? {
        val offset =
            if (nowInPoland().timeZone.useDaylightTime()) TimeUnit.HOURS.toMillis(2)
            else TimeUnit.HOURS.toMillis(1)

        return if (chartData.isNullOrEmpty()) null
        else {
            var returnList = chartData.filterNotNull()
            if (isPressure) returnList = returnList.filter { it[1] > 0 }
            returnList.map { ChartDataModel(it[0].toLong().plus(offset), it[1]) }
        }
    }

    private fun getSensors(stationId: Int): List<AirSensorModel>? {
        LspController.getAirSensors(stationId).apply {
            return if (isSuccessful) body()
                ?.filter { airSensor -> airSensor.id != null }
                ?.map { airSensor ->
                    AirSensorModel(
                        airSensor.id,
                        airSensor.param?.paramName,
                        airSensor.param?.paramCode,
                        parseAirChartData(LspController.getAirSensorData(airSensor.id!!).body())
                    )
                }
            else throw Exception(errorBody().toString())
        }
    }

    private fun getOverallAirQualityIndex(stationId: Int): Int? {
        LspController.getAirQualityIndex(stationId).apply {
            return if (isSuccessful) body()?.stIndexLevel?.id
            else throw Exception(errorBody().toString())
        }
    }


    private fun parseAirChartData(airSensorData: AirSensorData?): List<ChartDataModel>? {
        return if (airSensorData?.key == AirStationModel.AirSensorType.CO.paramKey) {
            airSensorData.values?.reversed()?.map { sensorValue ->
                ChartDataModel(
                    parseAirDate(sensorValue.date)?.time,
                    sensorValue.value?.div(1000).round(1)
                )
            }
        } else
            airSensorData?.values?.reversed()?.map { sensorValue ->
                ChartDataModel(
                    parseAirDate(sensorValue.date)?.time,
                    sensorValue.value
                )
            }
    }

    private fun parseAirDate(stringDate: String?): Date? =
        stringDate?.let { dateFormat("yyyy-MM-dd HH:mm:ss").parse(it) }
}