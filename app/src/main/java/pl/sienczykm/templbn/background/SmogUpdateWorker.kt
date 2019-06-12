package pl.sienczykm.templbn.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.DataModelDb
import pl.sienczykm.templbn.db.model.SmogSensorDb
import pl.sienczykm.templbn.db.model.SmogStationDb
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.remote.model.SmogSensorData
import pl.sienczykm.templbn.utils.SmogStation
import java.text.SimpleDateFormat
import java.util.*

class SmogUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val stationId = inputData.getInt(SmogStation.ID_KEY, 0)

        return if (stationId != 0) {
            AppDb.getDatabase(applicationContext).smogStationDao()
                .insert(SmogStationDb(stationId, getSensors(stationId)))

            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun getSensors(stationId: Int): List<SmogSensorDb>? {
        return LspController.getSmogSensors(stationId).body()
            ?.filter { smogSensor -> smogSensor.id != null }
            ?.map { smogSensor ->
                SmogSensorDb(
                    smogSensor.id,
                    smogSensor.param?.paramName,
                    smogSensor.param?.paramCode,
                    parseSensorData(LspController.getSmogSensorData(smogSensor.id!!).body())
                )
            }
    }

    private fun parseSensorData(smogSensorData: SmogSensorData?): List<DataModelDb>? {
        return smogSensorData?.values?.map { DataModelDb(parseDate(it.date), it.value) }
    }

    private fun parseDate(stringDate: String?): Long? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return inputFormat.parse(stringDate).time
    }
}