package pl.sienczykm.templbn.background

import android.content.Context
import androidx.annotation.WorkerThread
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.DataModelDb
import pl.sienczykm.templbn.db.model.SmogSensorDb
import pl.sienczykm.templbn.db.model.SmogStationDb
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.remote.model.SmogSensorData
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

object SmogProcessingUtils {
    @WorkerThread
    fun updateSmogStation(appContext: Context, stationId: Int) {
        try {
            AppDb.getDatabase(appContext).smogStationDao()
                .insert(SmogStationDb(stationId, getSensors(stationId)))
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun getSensors(stationId: Int): List<SmogSensorDb>? {

        val response = LspController.getSmogSensors(stationId)

        return when {
            response.isSuccessful -> response.body()
                ?.filter { smogSensor -> smogSensor.id != null }
                ?.map { smogSensor ->
                    SmogSensorDb(
                        smogSensor.id,
                        smogSensor.param?.paramName,
                        smogSensor.param?.paramCode,
                        parseSensorData(LspController.getSmogSensorData(smogSensor.id!!).body())
                    )
                }
            else -> throw Exception(response.errorBody().toString())
        }
    }

    private fun parseSensorData(smogSensorData: SmogSensorData?): List<DataModelDb>? {
        return smogSensorData?.values?.map { DataModelDb(parseSmogDate(it.date), it.value) }
    }

    private fun parseSmogDate(stringDate: String?): Long? {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("pl", "PL"))
        inputFormat.timeZone = TimeZone.getTimeZone("Europe/Warsaw")

        return inputFormat.parse(stringDate).time
    }
}