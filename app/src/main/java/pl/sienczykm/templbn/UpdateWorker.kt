package pl.sienczykm.templbn

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.TempStationDb
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.utils.Station
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class UpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

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
                    responseStationOne?.rainToday
                )

                Timber.e(AppDb.getDatabase(applicationContext).tempStationDao().insert(stationOne).toString())

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
                    responseStationOne?.rainToday
                )

                Timber.e(AppDb.getDatabase(applicationContext).tempStationDao().insert(stationTwo).toString())
            }
        }


        val dbStations: List<TempStationDb>? =
            AppDb.getDatabase(applicationContext).tempStationDao().getAllStations()

        Timber.e(dbStations.toString())

        return Result.success()
    }

    fun parseDate(stringDate: String?): Date {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return inputFormat.parse(stringDate)

    }
}