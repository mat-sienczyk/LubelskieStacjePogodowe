package pl.sienczykm.templbn.bg

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

        AppDb.getDatabase(applicationContext).tempStationDao()
            .insertStations(WeatherStation.STATIONS.map { weatherStation ->
                if (weatherStation.type == WeatherStation.Type.ONE) {
                    val responseStationOne = LspController.getStationOne(weatherStation).body()

                    TempStationDb(
                        weatherStation.id,
                        parseDate(responseStationOne?.data),
                        responseStationOne?.temperature,
                        responseStationOne?.temperatureWindChill,
                        null,
                        responseStationOne?.windSpeed,
                        responseStationOne?.windDir,
                        responseStationOne?.humidity,
                        responseStationOne?.pressure,
                        responseStationOne?.rainToday,
                        parseChartData(responseStationOne?.temperatureData?.data),
                        parseChartData(responseStationOne?.humidityData?.data),
                        parseChartData(responseStationOne?.windSpeedData?.data),
                        parseChartData(responseStationOne?.temperatureWindChart?.data),
                        parseChartData(responseStationOne?.pressureData?.data, true),
                        parseChartData(responseStationOne?.rainData?.data)
                    )

                } else {
                    val responseStationOne = LspController.getStationTwo(weatherStation).body()

                    TempStationDb(
                        weatherStation.id,
                        parseDate(responseStationOne?.data),
                        responseStationOne?.temperature,
                        null,
                        responseStationOne?.temperatureGround,
                        responseStationOne?.windSpeed,
                        responseStationOne?.windDir,
                        responseStationOne?.humidity,
                        null,
                        responseStationOne?.rainToday,
                        parseChartData(responseStationOne?.temperatureData?.data),
                        parseChartData(responseStationOne?.humidityData?.data)
                    )
                }
            })

//        val dbStations: List<TempStationDb>? =
//            AppDb.getDatabase(applicationContext).tempStationDao().getAllStations()
//        dbStations?.forEach { Timber.e(it.toString()) }

        return Result.success()
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