package pl.sienczykm.templbn

import android.app.Application
import pl.sienczykm.templbn.utils.handleNightMode
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        handleNightMode()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}