package pl.sienczykm.templbn.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.sienczykm.templbn.db.dao.SmogStationDao
import pl.sienczykm.templbn.db.dao.WeatherStationDao
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.utils.Config

@Database(entities = [WeatherStationModel::class, SmogStationModel::class], version = Config.DB_VERSION)
@TypeConverters(Converter::class)
abstract class AppDb : RoomDatabase() {

    abstract fun weatherStationDao(): WeatherStationDao

    abstract fun smogStationDao(): SmogStationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getDatabase(context: Context): AppDb {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    Config.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}