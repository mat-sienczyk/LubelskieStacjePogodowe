package pl.sienczykm.templbn.remote

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.sienczykm.templbn.model.SmogSensor
import pl.sienczykm.templbn.model.SmogSensorData
import pl.sienczykm.templbn.model.TempStationOne
import pl.sienczykm.templbn.model.TempStationTwo
import pl.sienczykm.templbn.utils.Config
import pl.sienczykm.templbn.utils.SmogStation
import pl.sienczykm.templbn.utils.WeatherStation
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object LspController {

    @WorkerThread
    fun getStationOne(stationId: Int): Response<TempStationOne> {
        return getWeatherService().getStationOne(stationId).execute()
    }

    @WorkerThread
    fun getStationTwo(stationId: Int): Response<TempStationTwo> {
        return getWeatherService().getStationTwo(stationId).execute()
    }

    @WorkerThread
    fun getSensors(stationId: Int): Response<List<SmogSensor>> {
        return getSmogService().getSensorsForStation(stationId).execute()
    }

    @WorkerThread
    fun getSensorData(sensorId: Int): Response<SmogSensorData> {
        return getSmogService().getDataForSensor(sensorId).execute()
    }

    private fun getWeatherService(): WeatherService {

        val retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_WEATHER_URL)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .build()

        return retrofit.create(WeatherService::class.java)
    }

    private fun getSmogService(): SmogService {

        val retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_SMOG_URL)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .build()

        return retrofit.create(SmogService::class.java)
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