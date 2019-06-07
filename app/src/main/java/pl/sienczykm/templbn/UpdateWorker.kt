package pl.sienczykm.templbn

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.ChartModelDb
import pl.sienczykm.templbn.db.model.TempStationDb
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.utils.Constants
import pl.sienczykm.templbn.utils.Station
import java.text.SimpleDateFormat
import java.util.*

class UpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val tempStation = mutableListOf<TempStationDb>()

        Station.STATIONS.forEach {

            if (it.parser == 1) {
                val responseStationOne = LspController.getStationOne(it).body()
                val stationOne = TempStationDb(
                    it.id,
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

                tempStation.add(stationOne)
//                Timber.e(AppDb.getDatabase(applicationContext).tempStationDao().insert(stationOne).toString())

            } else {
                val responseStationOne = LspController.getStationTwo(it).body()
                val stationTwo = TempStationDb(
                    it.id,
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

                tempStation.add(stationTwo)
//                Timber.e(AppDb.getDatabase(applicationContext).tempStationDao().insert(stationTwo).toString())
            }
        }

        AppDb.getDatabase(applicationContext).tempStationDao().insertStations(tempStation).toString()

//        val dbStations: List<TempStationDb>? =
//            AppDb.getDatabase(applicationContext).tempStationDao().getAllStations()
//
//        dbStations?.forEach { Timber.e(it.pressureChart?.toString()) }

        return Result.success()
    }

    private fun parseDate(stringDate: String?): Date? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return inputFormat.parse(stringDate)

    }

    private fun parseChartData(chartData: List<List<Double>?>?): List<ChartModelDb>? {
        return parseChartData(chartData, false)
    }

    private fun parseChartData(chartData: List<List<Double>?>?, isPressure: Boolean): List<ChartModelDb>? {
        val isDaylightTime = TimeZone.getTimeZone("Europe/Warsaw").inDaylightTime(Date())
        val offset = if (isDaylightTime) Constants.TWO_HOURS else Constants.ONE_HOUR

        return if (chartData.isNullOrEmpty()) null
        else {
            var returnList = chartData.filterNotNull()
            if (isPressure) returnList = returnList.filter { it[1] > 0 }
            returnList.map { ChartModelDb(it[0].toLong().plus(offset), it[1]) }
        }
    }
}