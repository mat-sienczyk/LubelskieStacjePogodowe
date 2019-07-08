package pl.sienczykm.templbn.background

import android.content.Context
import androidx.annotation.WorkerThread
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirSensorModel
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.remote.model.AirSensorData
import pl.sienczykm.templbn.utils.Constants
import pl.sienczykm.templbn.utils.round
import java.text.SimpleDateFormat
import java.util.*

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
        station.date = Date(station.sensors?.firstOrNull()?.data?.first{ it.value != null }?.timestamp!!)
        return station
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
                        station.temperatureData = parseChartData(responseStation?.temperatureData?.data)
                        station.humidityData = parseChartData(responseStation?.humidityData?.data)
                        station.windSpeedData = parseChartData(responseStation?.windSpeedData?.data)
                        station.temperatureWindData =
                            parseChartData(responseStation?.temperatureWindChart?.data)
                        station.pressureData = parseChartData(responseStation?.pressureData?.data, true)
                        station.rainTodayData = parseChartData(responseStation?.rainData?.data)

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
                        station.temperatureWindData = parseChartData(responseStation?.temperatureData?.data)
                        station.humidityData = parseChartData(responseStation?.humidityData?.data)


                    }
                    else -> throw Exception(response.errorBody().toString())
                }
            }
        }

        return station
    }

    private fun parseWeatherDate(stringDate: String?): Date? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        return inputFormat.parse(stringDate)
    }

    private fun parseChartData(chartData: List<List<Double>?>?): List<ChartDataModel>? {
        return parseChartData(chartData, false)
    }

    private fun parseChartData(chartData: List<List<Double>?>?, isPressure: Boolean): List<ChartDataModel>? {
        val isDaylightTime = TimeZone.getTimeZone("Europe/Warsaw").inDaylightTime(Date())
        val offset = if (isDaylightTime) Constants.TWO_HOURS else Constants.ONE_HOUR

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
                        parseSensorData(LspController.getAirSensorData(airSensor.id!!).body())
                    )
                }
            else -> throw Exception(response.errorBody().toString())
        }
    }

    private fun parseSensorData(airSensorData: AirSensorData?): List<ChartDataModel>? {
        return if (airSensorData?.key == AirStationModel.AirSensorType.CO.paramKey) {
            airSensorData.values?.reversed()?.map { sensorValue ->
                ChartDataModel(
                    parseAirDate(sensorValue.date),
                    sensorValue.value?.div(1000).round(1)
                )
            }
        } else
            airSensorData?.values?.reversed()?.map { sensorValue ->
                ChartDataModel(
                    parseAirDate(sensorValue.date),
                    sensorValue.value
                )
            }
    }

    private fun parseAirDate(stringDate: String?): Long? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return inputFormat.parse(stringDate).time
    }
}