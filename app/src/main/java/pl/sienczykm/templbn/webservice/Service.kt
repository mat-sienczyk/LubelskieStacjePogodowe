package pl.sienczykm.templbn.webservice

import okhttp3.ResponseBody
import pl.sienczykm.templbn.webservice.model.air.AirIndexQuality
import pl.sienczykm.templbn.webservice.model.air.AirSensor
import pl.sienczykm.templbn.webservice.model.air.AirSensorData
import pl.sienczykm.templbn.webservice.model.air.LookO2Station
import pl.sienczykm.templbn.webservice.model.weather.PogodynkaWeatherStation
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationOne
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationTwo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface Service {
    @GET
    fun getUmcsStationOne(
        @Url url: String,
        @Query("s") stationId: Int
    ): Call<UmcsWeatherStationOne>

    @GET
    fun getUmcsStationTwo(
        @Url url: String,
        @Query("s") stationId: Int
    ): Call<UmcsWeatherStationTwo>

    @GET
    fun getImgwStation(@Url url: String): Call<ResponseBody>

    @GET
    fun getPogodynkaStation(
        @Url url: String,
        @Query("id") stationId: Int
    ): Call<PogodynkaWeatherStation>

    @GET
    fun getSensorsForStation(@Url url: String): Call<List<AirSensor>>

    @GET
    fun getDataForSensor(@Url url: String): Call<AirSensorData>

    @GET
    fun getAirQualityIndex(@Url url: String): Call<AirIndexQuality>

    @GET
    fun getLookO2Stations(@Url url: String): Call<List<LookO2Station>>
}