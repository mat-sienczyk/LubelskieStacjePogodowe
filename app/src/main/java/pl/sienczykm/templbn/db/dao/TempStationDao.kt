package pl.sienczykm.templbn.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.sienczykm.templbn.db.model.TempStationDb

@Dao
interface TempStationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: TempStationDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(vararg stations: TempStationDb): List<Long>

    @Update
    fun update(station: TempStationDb)

    @Delete
    fun delete(station: TempStationDb)

    @Query("SELECT * FROM TempStationDb WHERE stationId LIKE :id")
    fun getStationById(id: Int): TempStationDb

    @Query("SELECT * FROM TempStationDb")
    fun getAllStations(): List<TempStationDb>

    @Query("DELETE FROM TempStationDb")
    fun deleteAllStations()

}