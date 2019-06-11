package pl.sienczykm.templbn.bg

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.SmogStationDb
import pl.sienczykm.templbn.db.model.TempStationDb
import timber.log.Timber

class GetStationsWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val smogStations: List<SmogStationDb>? =
            AppDb.getDatabase(applicationContext).smogStationDao().getAllStations()
        smogStations?.forEach { Timber.e(it.stationId.toString()) }

        val weatherStations: List<TempStationDb>? =
            AppDb.getDatabase(applicationContext).tempStationDao().getAllStations()
        weatherStations?.forEach { Timber.e(it.stationId.toString()) }

        return Result.success()

    }


}