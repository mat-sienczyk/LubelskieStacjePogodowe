package pl.sienczykm.templbn.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.DataModelDb
import pl.sienczykm.templbn.db.model.TempStationDb
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.utils.Constants
import pl.sienczykm.templbn.utils.WeatherStation
import java.text.SimpleDateFormat
import java.util.*

class WeatherUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val stationId = inputData.getInt(WeatherStation.ID_KEY, 0)

        return if (stationId != 0) {
            AppDb.getDatabase(applicationContext).tempStationDao()
                .insert(
                    if (WeatherStation.getStationForGivenId(stationId).type == WeatherStation.Type.ONE) {
                        val responseStation = LspController.getStationOne(stationId).body()

                        TempStationDb(
                            stationId,
                            parseDate(responseStation?.data),
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

                    } else {
                        val responseStation = LspController.getStationTwo(stationId).body()

                        TempStationDb(
                            stationId,
                            parseDate(responseStation?.data),
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
                )

//        val dbStations: List<TempStationDb>? =
//            AppDb.getDatabase(applicationContext).tempStationDao().getAllStations()
//        dbStations?.forEach { Timber.e(it.toString()) }

            return Result.success()
        } else {
            return Result.failure()
        }
    }

    private fun parseDate(stringDate: String?): Date? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

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
}