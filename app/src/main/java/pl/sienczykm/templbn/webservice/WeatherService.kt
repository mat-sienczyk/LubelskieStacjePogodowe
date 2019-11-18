package pl.sienczykm.templbn.webservice

import pl.sienczykm.templbn.webservice.model.WeatherStationOne
import pl.sienczykm.templbn.webservice.model.WeatherStationTwo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data.php")
    fun getStationOne(@Query("s") stationId: Int): Call<WeatherStationOne>

    @GET("data2.php")
    fun getStationTwo(@Query("s") stationId: Int): Call<WeatherStationTwo>
}