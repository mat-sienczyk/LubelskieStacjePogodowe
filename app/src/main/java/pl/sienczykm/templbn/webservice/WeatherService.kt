package pl.sienczykm.templbn.webservice

import okhttp3.ResponseBody
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationOne
import pl.sienczykm.templbn.webservice.model.weather.UmcsWeatherStationTwo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    @GET("data.php")
    fun getUmcsStationOne(@Query("s") stationId: Int): Call<UmcsWeatherStationOne>

    @GET("data2.php")
    fun getUmcsStationTwo(@Query("s") stationId: Int): Call<UmcsWeatherStationTwo>

    @GET("{id}")
    fun getImgwStation(@Path("id") stationId: Int): Call<ResponseBody>

    @GET("api/station/meteo")
    fun getPogodynkaStation(@Query("id") stationId: Int): Call<ResponseBody>
}