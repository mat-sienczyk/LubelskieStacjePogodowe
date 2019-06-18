package pl.sienczykm.templbn.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.sienczykm.templbn.db.model.SmogStationModel

@Dao
interface SmogStationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: SmogStationModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stations: List<SmogStationModel>): List<Long>

    @Update
    fun update(station: SmogStationModel)

    @Delete
    fun delete(station: SmogStationModel)

    @Query("SELECT * FROM SmogStationModel WHERE stationId LIKE :id")
    fun getStationById(id: Int): LiveData<SmogStationModel>

    @Query("SELECT * FROM SmogStationModel")
    fun getAllStations(): LiveData<List<SmogStationModel>>

    @Query("DELETE FROM SmogStationModel")
    fun deleteAllStations()

}