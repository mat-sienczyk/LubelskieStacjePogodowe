package pl.sienczykm.templbn.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.sienczykm.templbn.db.model.AirStationModel

@Dao
interface AirStationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: AirStationModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stations: List<AirStationModel>): List<Long>

    @Update
    fun update(station: AirStationModel)

    @Query("UPDATE AirStationModel SET favorite = :favorite WHERE stationId LIKE :id")
    suspend fun updateFavorite(id: Int, favorite: Boolean): Int

    @Delete
    fun delete(station: AirStationModel)

    @Query("SELECT * FROM AirStationModel WHERE stationId LIKE :id")
    fun getStationLiveDataById(id: Int): LiveData<AirStationModel>

    @Query("SELECT * FROM AirStationModel")
    fun getAllStationsLiveData(): LiveData<List<AirStationModel>>

    @Query("SELECT * FROM AirStationModel WHERE stationId LIKE :id")
    fun getStationById(id: Int): AirStationModel?

    @Query("SELECT * FROM AirStationModel")
    fun getAllStations(): List<AirStationModel>?

    @Query("DELETE FROM AirStationModel")
    fun deleteAllStations()

}