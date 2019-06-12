package pl.sienczykm.templbn.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.sienczykm.templbn.db.model.WeatherStationDb

@Dao
interface WeatherStationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: WeatherStationDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stations: List<WeatherStationDb>): List<Long>

    @Update
    fun update(station: WeatherStationDb)

    @Delete
    fun delete(station: WeatherStationDb)

    @Query("SELECT * FROM WeatherStationDb WHERE stationId LIKE :id")
    fun getStationById(id: Int): LiveData<WeatherStationDb>

    @Query("SELECT * FROM WeatherStationDb")
    fun getAllStations(): LiveData<List<WeatherStationDb>>

    @Query("DELETE FROM WeatherStationDb")
    fun deleteAllStations()

}