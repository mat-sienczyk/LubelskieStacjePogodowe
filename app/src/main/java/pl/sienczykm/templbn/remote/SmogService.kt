package pl.sienczykm.templbn.remote

import pl.sienczykm.templbn.model.SmogSensor
import pl.sienczykm.templbn.model.SmogSensorData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SmogService {
    @GET("station/sensors/{id}")
    fun getSensorsForStation(@Path("id") stationId: Int): Call<List<SmogSensor>>

    @GET("data/getData/{id}")
    fun getDataForSensor(@Path("id") sensorId: Int): Call<SmogSensorData>
}