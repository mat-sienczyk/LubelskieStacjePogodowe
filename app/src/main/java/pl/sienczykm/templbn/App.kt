package pl.sienczykm.templbn

import android.app.Application
import pl.sienczykm.templbn.db.AppDb
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    fun getDatabase(): AppDb {
        return AppDb.getDatabase(this)
    }

}