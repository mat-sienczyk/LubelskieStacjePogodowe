package pl.sienczykm.templbn.ui.station.common

import android.content.Context
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
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.chart_marker_view.view.*
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.ui.common.StationNavigator
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.dateFormat
import pl.sienczykm.templbn.utils.getColorCompact
import pl.sienczykm.templbn.utils.setColors
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber
import java.text.NumberFormat
import java.util.*

abstract class BaseStationFragment<K : BaseStationModel, T : BaseStationViewModel<K>, N : ViewDataBinding> :
    Fragment(),
    StationNavigator {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var viewModel: T
    lateinit var binding: N

    val timeChartFormatter = dateFormat("HH:mm")

    val valueChartFormatter: NumberFormat = NumberFormat.getInstance().apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }

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

        viewModel.station.observe(viewLifecycleOwner, Observer { station ->
            requireActivity().title = station.getName()
        })

        bottomSheetBehavior = BottomSheetBehavior.from(getBottomSheetLayout())
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }

        prepareChart()
    }

    private fun prepareChart() {
        getBottomSheetLayout().chart.apply {
            description.isEnabled = false
            setNoDataText(getString(R.string.chart_empty))
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            isScaleXEnabled = true
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = true
            setExtraOffsets(0f, 0f, 0f, 5f)
            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return timeChartFormatter.format(Date(value.toLong()))
                    }
                }
                textColor = requireContext().getColorCompact(R.color.base_color)
                textSize = 14f
                labelRotationAngle = -25f
                position = XAxis.XAxisPosition.BOTTOM
                granularity = (60 * 1000 * 10).toFloat()
            }
            axisLeft.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return valueChartFormatter.format(value)
                    }
                }
                textColor = requireContext().getColorCompact(R.color.base_color)
                textSize = 14f
                setLabelCount(7, true)
                setDrawZeroLine(true)
            }
            axisRight.apply { isEnabled = false }
            legend.isEnabled = false
        }
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
        viewModel.handleFavorite()
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

    override fun showChart(
        chartData: List<ChartDataModel>,
        minIsZero: Boolean,
        limitValue: Float?,
        unit: String,
        title: String?,
        @StringRes titleId: Int?
    ) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        getBottomSheetLayout().unit.text = unit
        getBottomSheetLayout().title.text =
            getString(R.string.chart_title, title ?: getString(titleId!!))

        val limitLine = limitValue?.let {
            LimitLine(it, getString(R.string.limit_line_label))
        } ?: LimitLine(0f)

        getBottomSheetLayout().chart.apply {
            clear()
            marker = MyMarkerView(requireContext(), R.layout.chart_marker_view, unit)
            axisLeft.apply {
                removeAllLimitLines()
                addLimitLine(limitLine.apply {
                    lineWidth = 2.5f
                    lineColor = requireContext().getColorCompact(R.color.base_color)
                    textColor = requireContext().getColorCompact(R.color.base_color)
                })
                resetAxisMinimum()
                when (minIsZero) {
                    true -> axisMinimum = 0f
                    false -> {
                        spaceTop = 2f
                        spaceBottom = 2f
                    }
                }
            }
            data = LineData(LineDataSet(chartData.map {
                Entry(
                    it.timestamp!!.toFloat(),
                    it.value!!.toFloat()
                )
            }, null).apply {
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawCircles(false)
                setDrawValues(false)
                color = requireContext().getColorCompact(R.color.colorAccent)
                lineWidth = 1.5f
                highLightColor = requireContext().getColorCompact(R.color.colorPrimary)
                highlightLineWidth = 0.8f
                setDrawFilled(true)
                fillColor = requireContext().getColorCompact(R.color.colorAccent)
            })
            fitScreen()
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun handleError(message: String?) {
        Timber.e(Throwable(message))
        showSnackbar(R.string.error_server)
    }

    override fun showInfo(message: Int) {
        showSnackbar(message)
    }

    override fun noConnection() {
        showSnackbar(R.string.error_no_connection)
    }

    private fun showSnackbar(@StringRes message: Int) {
        getCoordinatorLayout().snackbarShow(message)
    }

    inner class MyMarkerView(context: Context, res: Int, val unit: String) :
        MarkerView(context, res) {

        init {
            marker_bg.background = getDrawable(
                R.drawable.ic_marker,
                context.getColorCompact(R.color.colorPrimary)
            )
        }

        private var myOffset: MPPointF? = null

        override fun refreshContent(e: Entry, highlight: Highlight) {

            val time = timeChartFormatter.format(Date(highlight.x.toLong()))

            val value = valueChartFormatter.format(highlight.y) + unit

            content.text = getString(R.string.marker_text, time, value)

            super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF? {
            if (myOffset == null) {
                myOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
            }
            return myOffset
        }
    }
}

