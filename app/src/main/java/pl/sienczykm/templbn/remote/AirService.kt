package pl.sienczykm.templbn.remote

import pl.sienczykm.templbn.remote.model.AirSensor
import pl.sienczykm.templbn.remote.model.AirSensorData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AirService {
    @GET("station/sensors/{id}")
    fun getSensorsForStation(@Path("id") stationId: Int): Call<List<AirSensor>>

    @GET("data/getData/{id}")
    fun getDataForSensor(@Path("id") sensorId: Int): Call<AirSensorData>
}