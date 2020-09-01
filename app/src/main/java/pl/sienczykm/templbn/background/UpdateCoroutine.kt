package pl.sienczykm.templbn.background

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.*
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.ExternalDisplaysHandler
import pl.sienczykm.templbn.utils.isNetworkAvailable

object UpdateCoroutine {
    fun updateAir(
        context: Context,
        stationsIds: List<Int>,
        statusReceiver: StatusReceiver
    ) = update(context, stationsIds, statusReceiver, AirStationModel.ID_KEY)

    fun updateWeather(
        context: Context,
        stationsIds: List<Int>,
        statusReceiver: StatusReceiver
    ) = update(context, stationsIds, statusReceiver, WeatherStationModel.ID_KEY)

    private fun update(
        context: Context,
        stationIds: List<Int>,
        statusReceiver: StatusReceiver,
        stationType: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            statusReceiver.send(StatusReceiver.STATUS_REFRESHING, Bundle())

            if (context.isNetworkAvailable()) {

                val errorList = mutableListOf<String>()

                val jobs = stationIds.map { stationId ->
//                stationIds.forEach { stationId ->
                    async {
                        try {
                            when (stationType) {
                                AirStationModel.ID_KEY -> ProcessingUtils.updateAirStation(
                                    context,
                                    stationId
                                )
                                WeatherStationModel.ID_KEY -> ProcessingUtils.updateWeatherStation(
                                    context,
                                    stationId
                                )
                                else -> errorList.add("Invalid station key")
                            }
                        } catch (e: Exception) {
                            errorList.add(e.message ?: "Unknown exception")
                        }
                    }
                }
                jobs.awaitAll()

                if (errorList.isNotEmpty()) {
                    val errorBundle = Bundle().apply {
                        putStringArray(ProcessingUtils.ERROR_KEY, errorList.toTypedArray())
                    }
                    statusReceiver.send(StatusReceiver.STATUS_ERROR, errorBundle)
                }

                ExternalDisplaysHandler.updateExternalDisplays(context)
            } else {
                statusReceiver.send(StatusReceiver.STATUS_NO_CONNECTION, Bundle())
            }
            statusReceiver.send(StatusReceiver.STATUS_IDLE, Bundle())
        }
    }
}