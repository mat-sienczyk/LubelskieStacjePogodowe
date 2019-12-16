package pl.sienczykm.templbn.webservice

import pl.sienczykm.templbn.webservice.model.AirIndexQuality
import pl.sienczykm.templbn.webservice.model.AirSensor
import pl.sienczykm.templbn.webservice.model.AirSensorData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AirService {
    @GET("station/sensors/{id}")
    fun getSensorsForStation(@Path("id") stationId: Int): Call<List<AirSensor>>

    @GET("data/getData/{id}")
    fun getDataForSensor(@Path("id") sensorId: Int): Call<AirSensorData>

    @GET("aqindex/getIndex/{id}")
    fun getAirQualityIndex(@Path("id") stationId: Int): Call<AirIndexQuality>
}