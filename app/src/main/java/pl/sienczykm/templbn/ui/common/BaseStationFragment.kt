package pl.sienczykm.templbn.ui.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.StationModel
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber

abstract class BaseStationFragment<K : StationModel, T : BaseStationViewModel<K>, N : ViewDataBinding> : Fragment(),
    StationNavigator {

    lateinit var viewModel: T
    lateinit var binding: N

    abstract fun getViewModel(stationId: Int): T

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getSwipeToRefreshLayout(): SwipeRefreshLayout

    abstract fun getCoordinatorLayout(): CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stationId = arguments?.getInt(StationActivity.STATION_ID_KEY, 0)!!

        super.onCreate(savedInstanceState)
        viewModel = getViewModel(stationId)
        viewModel.setNavigator(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSwipeToRefreshLayout().setOnRefreshListener {
            viewModel.refresh()
        }

        getSwipeToRefreshLayout().setColorSchemeColors(
            resources.getColor(R.color.main_yellow),
            resources.getColor(R.color.main_red),
            resources.getColor(R.color.main_green)
        )

        viewModel.station.observe(this, Observer { activity?.title = it.name })
    }

    override fun openCustomTab(url: String?) {
        if (Patterns.WEB_URL.matcher(url).matches()) {
            val webPage = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                val builder = CustomTabsIntent.Builder()
//                builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(activity, webPage)
            } else {
                showError(R.string.error_no_web_browser)
            }
        }
    }

    override fun handleError(message: String?) {
        Timber.e(Throwable(message))
        showError(R.string.error_server)
    }

    override fun noConnection() {
        showError(R.string.error_no_connection)
    }

    private fun showError(@StringRes message: Int) {
        snackbarShow(getCoordinatorLayout(), message)
    }
}