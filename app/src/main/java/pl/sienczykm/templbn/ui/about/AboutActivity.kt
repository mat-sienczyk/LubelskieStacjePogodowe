package pl.sienczykm.templbn.ui.about

import android.os.Bundle
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.ui.common.ActivityWithToolbarAndUpAction

class AboutActivity : ActivityWithToolbarAndUpAction() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, AboutFragment.newInstance())
            .commit()
    }

}