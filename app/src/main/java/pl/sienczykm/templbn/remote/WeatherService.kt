package pl.sienczykm.templbn.remote

import pl.sienczykm.templbn.model.TempStationOne
import pl.sienczykm.templbn.model.TempStationTwo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data.php")
    fun getStationOne(@Query("s") stationId: Int): Call<TempStationOne>

    @GET("data2.php")
    fun getStationTwo(@Query("s") stationId: Int): Call<TempStationTwo>
}