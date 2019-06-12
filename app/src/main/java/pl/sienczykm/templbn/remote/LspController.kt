package pl.sienczykm.templbn.remote

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.sienczykm.templbn.remote.model.SmogSensor
import pl.sienczykm.templbn.remote.model.SmogSensorData
import pl.sienczykm.templbn.remote.model.WeatherStationOne
import pl.sienczykm.templbn.remote.model.WeatherStationTwo
import pl.sienczykm.templbn.utils.Config
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object LspController {

    @WorkerThread
    fun getWeatherStationOne(stationId: Int): Response<WeatherStationOne> {
        return getWeatherService().getStationOne(stationId).execute()
    }

    @WorkerThread
    fun getWeatherStationTwo(stationId: Int): Response<WeatherStationTwo> {
        return getWeatherService().getStationTwo(stationId).execute()
    }

    @WorkerThread
    fun getSmogSensors(stationId: Int): Response<List<SmogSensor>> {
        return getSmogService().getSensorsForStation(stationId).execute()
    }

    @WorkerThread
    fun getSmogSensorData(sensorId: Int): Response<SmogSensorData> {
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