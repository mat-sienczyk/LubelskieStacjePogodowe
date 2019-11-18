package pl.sienczykm.templbn.background

import android.content.Context
import androidx.annotation.WorkerThread
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirSensorModel
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.webservice.LspController
import pl.sienczykm.templbn.webservice.model.AirSensorData
import pl.sienczykm.templbn.utils.dateFormat
import pl.sienczykm.templbn.utils.nowInPoland
import pl.sienczykm.templbn.utils.round
import java.util.*
import java.util.concurrent.TimeUnit

object ProcessingUtils {

    const val ERROR_KEY = "error_key"

    @WorkerThread
    fun updateAirStation(appContext: Context, stationId: Int) {

        val dao = AppDb.getDatabase(appContext).airStationDao()

        //TODO przemodelować to żeby nie trzeba było pobierać stacji z bazy najpierw (update nowych danych, upsert w dao?)
        val station = dao.getStationById(stationId)

        if (station != null) {
            dao.insert(constructAirStationModel(station))
        } else {
            dao.insert(constructAirStationModel(AirStationModel.getStationForGivenId(stationId)))
        }
    }

    @WorkerThread
    fun updateWeatherStation(appContext: Context, stationId: Int) {

        val dao = AppDb.getDatabase(appContext).weatherStationDao()

        //TODO przemodelować to żeby nie trzeba było pobierać stacji z bazy najpierw (update nowych danych, upsert w dao?)
        val station = dao.getStationById(stationId)

        if (station != null) {
            dao.insert(constructWeatherStationModel(station))
        } else {
            dao.insert(constructWeatherStationModel(WeatherStationModel.getStationForGivenId(stationId)))
        }
    }

    private fun constructAirStationModel(station: AirStationModel): AirStationModel {
        station.sensors = getSensors(station.stationId)
        station.date = getAirStationDate(station.sensors)
        return station
    }

    private fun getAirStationDate(sensors: List<AirSensorModel>?): Date? {
        return sensors?.filter { sensorModel -> sensorModel.data?.isNotEmpty()!! }
            ?.maxBy { sensorModel -> sensorModel.data?.lastOrNull { dataModel -> dataModel.value != null }?.timestamp!! }
            ?.data?.lastOrNull { dataModel -> dataModel.value != null }?.timestamp?.let { Date(it) }
    }

    private fun constructWeatherStationModel(
        station: WeatherStationModel
    ): WeatherStationModel {
        when (station.type) {
            WeatherStationModel.Type.ONE -> {

                val response = LspController.getWeatherStationOne(station.stationId)

                when {
                    response.isSuccessful -> {
                        val responseStation = response.body()

                        station.date = parseWeatherDate(responseStation?.data)
                        station.temperature = responseStation?.temperature
                        station.temperatureWind = responseStation?.temperatureWindChill
                        station.windSpeed = responseStation?.windSpeed
                        station.windDir = responseStation?.windDir
                        station.humidity = responseStation?.humidity
                        station.pressure = responseStation?.pressure
                        station.rainToday = responseStation?.rainToday
                        station.temperatureData =
                            parseWeatherChartData(responseStation?.temperatureData?.data)
                        station.humidityData =
                            parseWeatherChartData(responseStation?.humidityData?.data)
                        station.windSpeedData =
                            parseWeatherChartData(responseStation?.windSpeedData?.data)
                        station.temperatureWindData =
                            parseWeatherChartData(responseStation?.temperatureWindChart?.data)
                        station.pressureData =
                            parseWeatherChartData(responseStation?.pressureData?.data, true)
                        station.rainTodayData =
                            parseWeatherChartData(responseStation?.rainData?.data)

                    }
                    else -> throw Exception(response.errorBody().toString())
                }

            }
            WeatherStationModel.Type.TWO -> {

                val response = LspController.getWeatherStationTwo(station.stationId)

                when {
                    response.isSuccessful -> {
                        val responseStation = response.body()

                        station.date = parseWeatherDate(responseStation?.data)
                        station.temperature = responseStation?.temperature
                        station.temperatureGround = responseStation?.temperatureGround
                        station.windSpeed = responseStation?.windSpeed
                        station.windDir = responseStation?.windDir
                        station.humidity = responseStation?.humidity
                        station.rainToday = responseStation?.rainToday
                        station.temperatureWindData =
                            parseWeatherChartData(responseStation?.temperatureData?.data)
                        station.humidityData =
                            parseWeatherChartData(responseStation?.humidityData?.data)


                    }
                    else -> throw Exception(response.errorBody().toString())
                }
            }
        }

        return station
    }

    private fun parseWeatherDate(stringDate: String?): Date? =
        stringDate?.let { dateFormat("yyyy-MM-dd HH:mm", "UTC").parse(it) }

    private fun parseWeatherChartData(
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

        val response = LspController.getAirSensors(stationId)

        return when {
            response.isSuccessful -> response.body()
                ?.filter { airSensor -> airSensor.id != null }
                ?.map { airSensor ->
                    AirSensorModel(
                        airSensor.id,
                        airSensor.param?.paramName,
                        airSensor.param?.paramCode,
                        parseAirChartData(LspController.getAirSensorData(airSensor.id!!).body())
                    )
                }
            else -> throw Exception(response.errorBody().toString())
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