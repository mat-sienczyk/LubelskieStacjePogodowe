package pl.sienczykm.templbn.background

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.*
import pl.sienczykm.templbn.utils.ExternalDisplaysHandler
import pl.sienczykm.templbn.utils.isNetworkAvailable

object UpdateCoroutine {
    fun updateAir(
        context: Context,
        stationsIds: List<Int>,
        statusReceiver: StatusReceiver
    ) = update(context, stationsIds, statusReceiver) { id ->
        ProcessingUtils.updateAirStation(context, id)
    }

    fun updateWeather(
        context: Context,
        stationsIds: List<Int>,
        statusReceiver: StatusReceiver
    ) = update(context, stationsIds, statusReceiver) { id ->
        ProcessingUtils.updateWeatherStation(context, id)
    }

    private fun update(
        context: Context,
        stationIds: List<Int>,
        statusReceiver: StatusReceiver,
        updateStation: (Int) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            statusReceiver.send(StatusReceiver.STATUS_REFRESHING, Bundle())

            if (context.isNetworkAvailable()) {

                val errorList = mutableListOf<String>()

                val jobs = stationIds.map { stationId ->
                    async { // can't do it fully async because server cutting me off, BUT IT"S WORKING
                        try {
                            updateStation(stationId)
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