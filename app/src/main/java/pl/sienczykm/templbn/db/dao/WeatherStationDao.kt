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

    @Delete
    fun delete(station: WeatherStationModel)

    @Query("SELECT * FROM WeatherStationModel WHERE stationId LIKE :id")
    fun getStationById(id: Int): LiveData<WeatherStationModel>

    @Query("SELECT * FROM WeatherStationModel")
    fun getAllStations(): LiveData<List<WeatherStationModel>>

    @Query("DELETE FROM WeatherStationModel")
    fun deleteAllStations()

}