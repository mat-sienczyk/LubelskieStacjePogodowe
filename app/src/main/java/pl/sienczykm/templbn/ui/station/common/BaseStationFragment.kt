package pl.sienczykm.templbn.ui.station.common

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.AppDb
import pl.sienczykm.templbn.db.model.AirStationModel
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.common.StationNavigator
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber

abstract class BaseStationFragment<K : BaseStationModel, T : BaseStationViewModel<K>, N : ViewDataBinding> : Fragment(),
    StationNavigator {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var viewModel: T
    lateinit var binding: N

    abstract fun getViewModel(stationId: Int): T

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getSwipeToRefreshLayout(): SwipeRefreshLayout

    abstract fun getCoordinatorLayout(): CoordinatorLayout

    abstract fun getBottomSheetLayout(): LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stationId = requireArguments().getInt(StationActivity.STATION_ID_KEY, 0)
        viewModel = getViewModel(stationId)
        viewModel.setNavigator(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSwipeToRefreshLayout().setOnRefreshListener {
            viewModel.refresh()
        }

        getSwipeToRefreshLayout().setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.main_yellow),
            ContextCompat.getColor(requireContext(), R.color.main_red),
            ContextCompat.getColor(requireContext(), R.color.main_green)
        )

        viewModel.station.observe(this, Observer { station ->
            requireActivity().title = station.name
        })

        bottomSheetBehavior = BottomSheetBehavior.from(getBottomSheetLayout())
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }
    }

    private fun updateFavorite(favoriteItem: MenuItem?, favorite: Boolean) {
        favoriteItem?.icon =
            if (favorite) getDrawable(R.drawable.ic_heart_solid) else getDrawable(R.drawable.ic_heart)
    }

    private fun getDrawable(@DrawableRes drawableResId: Int, @ColorInt color: Int = Color.WHITE): Drawable? {
        val originalDrawable = ContextCompat.getDrawable(requireContext(), drawableResId)
        val wrappedDrawable = originalDrawable?.let { DrawableCompat.wrap(it) } ?: return null
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.station_fragment_menu, menu)

        viewModel.station.observe(this, Observer { station ->
            updateFavorite(menu.findItem(R.id.favorite), station.favorite)
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite -> {
                handleFavoriteClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleFavoriteClick() {
        val station = viewModel.station.value
        val updated = when (station) {
            is WeatherStationModel -> AppDb.getDatabase(requireContext()).weatherStationDao().updateFavorite(
                station.stationId,
                !station.favorite
            )
            is AirStationModel -> AppDb.getDatabase(requireContext()).airStationDao().updateFavorite(
                station.stationId,
                !station.favorite
            )
            else -> throw Exception("Invalid station object")
        }

        if (updated > 0) {
            if (!station.favorite) showSnackbar(R.string.added_to_favorites) else showSnackbar(R.string.removed_from_favorites)
        }
    }

    override fun openCustomTab(url: String) {
        if (Patterns.WEB_URL.matcher(url).matches()) {
            val webPage = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                val builder = CustomTabsIntent.Builder()
//                builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(requireContext(), webPage)
            } else {
                showSnackbar(R.string.error_no_web_browser)
            }
        }
    }

    override fun showChart(chartData: List<ChartDataModel>) {
        getBottomSheetLayout().text.text = chartData.joinToString(separator = "\n")
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun handleError(message: String?) {
        Timber.e(Throwable(message))
        showSnackbar(R.string.error_server)
    }

    override fun noConnection() {
        showSnackbar(R.string.error_no_connection)
    }

    private fun showSnackbar(@StringRes message: Int) {
        getCoordinatorLayout().snackbarShow(message)
    }
}