package pl.sienczykm.templbn.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.sienczykm.templbn.db.model.WeatherStationModel

@Dao
interface WeatherStationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: WeatherStationModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stations: List<WeatherStationModel>): List<Long>

    @Update
    fun update(station: WeatherStationModel)

    @Query("UPDATE WeatherStationModel SET favorite = :favorite WHERE stationId LIKE :id")
    suspend fun updateFavorite(id: Int, favorite: Boolean): Int

    @Delete
    fun delete(station: WeatherStationModel)

    @Query("SELECT * FROM WeatherStationModel WHERE stationId LIKE :id")
    fun getStationLiveDataById(id: Int): LiveData<WeatherStationModel>

    @Query("SELECT * FROM WeatherStationModel")
    fun getAllStationsLiveData(): LiveData<List<WeatherStationModel>>

    @Query("SELECT * FROM WeatherStationModel WHERE stationId LIKE :id")
    fun getStationById(id: Int): WeatherStationModel?

    @Query("SELECT * FROM WeatherStationModel")
    fun getAllStations(): List<WeatherStationModel>?

    @Query("DELETE FROM WeatherStationModel")
    fun deleteAllStations()

}