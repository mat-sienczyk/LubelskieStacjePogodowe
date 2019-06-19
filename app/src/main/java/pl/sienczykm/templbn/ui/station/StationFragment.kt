package pl.sienczykm.templbn.ui.station

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentStationBinding
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber

class StationFragment : Fragment(), StationNavigator {

    lateinit var viewModel: StationViewModel
    lateinit var binding: FragmentStationBinding

    enum class Type {
        WEATHER, SMOG
    }

    companion object {

        const val STATION_TYPE_KEY = "station_type_key"
        const val STATION_ID_KEY = "station_id_key"

        fun newInstance(type: Type, stationId: Int): StationFragment {
            val args = Bundle()
            args.putSerializable(STATION_TYPE_KEY, type)
            args.putInt(STATION_ID_KEY, stationId)
            val fragment = StationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(StationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        viewModel.setNavigator(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_station, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getSerializable(STATION_TYPE_KEY) as Type
        val stationId = arguments?.getInt(STATION_ID_KEY)

        binding.swipeLayout.setOnRefreshListener {
            viewModel.refresh(type, stationId!!)
        }

        binding.swipeLayout.setColorSchemeColors(
            resources.getColor(R.color.main_yellow),
            resources.getColor(R.color.main_red),
            resources.getColor(R.color.main_green)
        )

        viewModel.refresh(type, stationId!!)
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
        snackbarShow(binding.coordinatorLayout, message)
    }
}