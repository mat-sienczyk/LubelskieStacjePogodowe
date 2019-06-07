package pl.sienczykm.templbn.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.sienczykm.templbn.db.dao.TempStationDao
import pl.sienczykm.templbn.db.model.TempStationDb
import pl.sienczykm.templbn.utils.Config

@Database(entities = [TempStationDb::class], version = Config.DB_VERSION)
@TypeConverters(Converter::class)
abstract class AppDb : RoomDatabase() {

    abstract fun tempStationDao(): TempStationDao

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
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}