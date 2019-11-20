package pl.sienczykm.templbn.ui.settings

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
}