package pl.sienczykm.templbn.ui.settings

import android.app.Activity
import android.os.Bundle
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.common.ActivityWithToolbarAndUpAction


class SettingsActivity : ActivityWithToolbarAndUpAction() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SettingsFragment.newInstance())
            .commit()
    }

    //TODO use Activity Result APIs in the future
    fun reloadMapFragment() {
        setResult(Activity.RESULT_OK)
    }
}