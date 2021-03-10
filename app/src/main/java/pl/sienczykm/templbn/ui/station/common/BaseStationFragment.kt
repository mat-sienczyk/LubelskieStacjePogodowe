package pl.sienczykm.templbn.ui.station.common

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.ui.common.StationNavigator
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.getDrawableWithColor
import pl.sienczykm.templbn.utils.openUrl
import pl.sienczykm.templbn.utils.setColors
import pl.sienczykm.templbn.utils.showSnackbar
import timber.log.Timber

abstract class BaseStationFragment<K : BaseStationModel, T : BaseStationViewModel<K>, N : ViewDataBinding> :
    Fragment(),
    StationNavigator {

    lateinit var viewModel: T
    lateinit var binding: N

    abstract fun getViewModel(stationId: Int): T

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getSwipeToRefreshLayout(): SwipeRefreshLayout

    abstract fun getCoordinatorLayout(): CoordinatorLayout

    abstract fun getBottomSheetLayout(): LinearLayout

    private lateinit var chartHandler: ChartHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stationId = requireArguments().getInt(StationActivity.STATION_ID_KEY, 0)
        viewModel = getViewModel(stationId)
        viewModel.setNavigator(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSwipeToRefreshLayout().apply {
            setOnRefreshListener {
                viewModel.refresh()
            }
            setColors()
        }

        viewModel.station.observe(viewLifecycleOwner, { station ->
            requireActivity().title = station?.getName()
        })

        chartHandler = ChartHandler(getBottomSheetLayout())
    }

    private fun updateFavorite(favoriteItem: MenuItem?, favorite: Boolean) {
        favoriteItem?.icon =
            if (favorite) requireContext().getDrawableWithColor(R.drawable.ic_heart_solid)
            else requireContext().getDrawableWithColor(R.drawable.ic_heart)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.station_fragment_menu, menu)

        viewModel.station.observe(viewLifecycleOwner, { station ->
            station?.run {
                updateFavorite(menu.findItem(R.id.favorite), favorite)
            }
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
        viewModel.handleFavorite()
    }

    override fun openCustomTab(url: String) {
        if (!requireContext().openUrl(url)) {
            showSnackbar(R.string.error_no_web_browser)
        }
    }

    override fun showChart(
        chartData: List<ChartDataModel>,
        minIsZero: Boolean,
        limitValue: Float?,
        unit: String,
        title: String?,
        @StringRes titleId: Int?
    ) {
        chartHandler.showChart(chartData, minIsZero, limitValue, unit, title, titleId)
    }

    override fun handleError(errorMessages: Array<String>?) {
        errorMessages?.forEach { Timber.e(Throwable(it)) }
        showSnackbar(R.string.error_server)
    }

    override fun showInfo(message: Int) {
        showSnackbar(message)
    }

    override fun noConnection() {
        showSnackbar(R.string.error_no_connection)
    }

    private fun showSnackbar(@StringRes message: Int) {
        getCoordinatorLayout().showSnackbar(message)
    }
}

