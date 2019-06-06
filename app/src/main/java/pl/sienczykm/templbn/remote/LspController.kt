package pl.sienczykm.templbn.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.sienczykm.templbn.model.TempStationOne
import pl.sienczykm.templbn.model.TempStationTwo
import pl.sienczykm.templbn.utils.Config
import pl.sienczykm.templbn.utils.Station
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object LspController {

    fun getStationOne(station: Station, callback: Callback<TempStationOne>) {
        getService().getStationOne(station.id).enqueue(callback)
    }

    fun getStationTwo(station: Station, callback: Callback<TempStationTwo>) {
        getService().getStationTwo(station.id).enqueue(callback)
    }

    private fun getService(): LspService {

            val retrofit = Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build()

            return retrofit.create(LspService::class.java)
        }


    private fun getClient(): OkHttpClient {

            val okHttpClientBuilder = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            return okHttpClientBuilder.build()
        }

    private fun getGson(): Gson {
        val gsonBuilder = GsonBuilder()
            .setLenient()

        return gsonBuilder.create()
    }
}