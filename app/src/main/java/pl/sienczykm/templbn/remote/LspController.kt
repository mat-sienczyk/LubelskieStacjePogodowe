package pl.sienczykm.templbn.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.sienczykm.templbn.model.TempStation
import pl.sienczykm.templbn.utils.Config
import pl.sienczykm.templbn.utils.Station
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LspController {

    companion object {
        fun getStation(station: Station, callback: Callback<TempStation>) {
            if (station.parser == 1) {
                getService().getStation1(station.id).enqueue(callback)
            } else {
                getService().getStation2(station.id).enqueue(callback)
            }
        }

        private fun getService(): LspService {

            val retrofit = Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(LspService::class.java)
        }


        private fun getClient(): OkHttpClient {

            val okHttpClientBuilder = OkHttpClient().newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            return okHttpClientBuilder.build()
        }
    }
}