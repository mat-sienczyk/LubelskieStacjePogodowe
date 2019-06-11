package pl.sienczykm.templbn.db.dao

import androidx.room.*
import pl.sienczykm.templbn.db.model.SmogStationDb

@Dao
interface SmogStationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: SmogStationDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stations: List<SmogStationDb>): List<Long>

    @Update
    fun update(station: SmogStationDb)

    @Delete
    fun delete(station: SmogStationDb)

    @Query("SELECT * FROM SmogStationDb WHERE stationId LIKE :id")
    fun getStationById(id: Int): SmogStationDb

    @Query("SELECT * FROM SmogStationDb")
    fun getAllStations(): List<SmogStationDb>

    @Query("DELETE FROM SmogStationDb")
    fun deleteAllStations()

}