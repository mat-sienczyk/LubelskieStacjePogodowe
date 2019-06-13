package pl.sienczykm.templbn.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.utils.SmogStation

class SmogUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val stationId = inputData.getInt(SmogStation.ID_KEY, 0)

        return if (stationId != 0) {
            try {
                ProcessingUtils.updateSmogStation(applicationContext, stationId)
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        } else {
            Result.failure()
        }
    }
}