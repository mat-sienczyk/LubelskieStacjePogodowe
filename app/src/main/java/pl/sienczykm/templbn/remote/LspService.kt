package pl.sienczykm.templbn.remote

import pl.sienczykm.templbn.model.TempStation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LspService {
    @GET("data.php")
    fun getStation1(@Query("s") stationId: Int): Call<TempStation>

    @GET("data2.php")
    fun getStation2(@Query("s") stationId: Int): Call<TempStation>
}