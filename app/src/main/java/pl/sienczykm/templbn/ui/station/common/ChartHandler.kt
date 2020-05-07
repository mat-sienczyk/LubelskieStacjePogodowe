package pl.sienczykm.templbn.ui.station.common

import android.content.Context
import android.widget.LinearLayout
import androidx.annotation.StringRes
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
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.ChartDataModel
import pl.sienczykm.templbn.utils.dateFormat
import pl.sienczykm.templbn.utils.getColorCompact
import pl.sienczykm.templbn.utils.getDrawableWithColor
import java.text.NumberFormat
import java.util.*

class ChartHandler(private val bottomSheetLayout: LinearLayout) {

    private val context = bottomSheetLayout.context

    private val timeChartFormatter = dateFormat("HH:mm")

    private val valueChartFormatter: NumberFormat = NumberFormat.getInstance().apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }

    private var bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

    init {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        prepareChart()
    }

    private fun prepareChart() {
        bottomSheetLayout.chart.apply {
            description.isEnabled = false
            setNoDataText(context.getString(R.string.chart_empty))
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
                textColor = context.getColorCompact(R.color.base_color)
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
                textColor = context.getColorCompact(R.color.base_color)
                textSize = 14f
                setLabelCount(7, true)
                setDrawZeroLine(true)
            }
            axisRight.apply { isEnabled = false }
            legend.isEnabled = false
        }
    }

    fun showChart(
        chartData: List<ChartDataModel>,
        minIsZero: Boolean,
        limitValue: Float?,
        unit: String,
        title: String?,
        @StringRes titleId: Int?
    ) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetLayout.unit.text = unit
        bottomSheetLayout.title.text =
            if (unit == context.getString(R.string.milliliters)) // TODO ugly?
                title ?: context.getString(titleId!!)
            else
                context.getString(R.string.chart_title, title ?: context.getString(titleId!!))

        val limitLine = limitValue?.let {
            LimitLine(it, context.getString(R.string.limit_line_label))
        } ?: LimitLine(0f)

        bottomSheetLayout.chart.apply {
            clear()
            marker = MyMarkerView(context, R.layout.chart_marker_view, unit)
            axisLeft.apply {
                removeAllLimitLines()
                addLimitLine(limitLine.apply {
                    lineWidth = 2.5f
                    lineColor = context.getColorCompact(R.color.base_color)
                    textColor = context.getColorCompact(R.color.base_color)
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
                color = context.getColorCompact(R.color.colorAccent)
                lineWidth = 1.5f
                highLightColor = context.getColorCompact(R.color.colorPrimary)
                highlightLineWidth = 0.8f
                setDrawFilled(true)
                fillColor = context.getColorCompact(R.color.colorAccent)
            })
            fitScreen()
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    inner class MyMarkerView(context: Context, res: Int, val unit: String) :
        MarkerView(context, res) {

        init {
            marker_bg.background = context.getDrawableWithColor(
                R.drawable.ic_marker,
                context.getColorCompact(R.color.colorPrimary)
            )
        }

        private var myOffset: MPPointF? = null

        override fun refreshContent(e: Entry, highlight: Highlight) {

            val time = timeChartFormatter.format(Date(highlight.x.toLong()))

            val value = valueChartFormatter.format(highlight.y) + unit

            content.text = context.getString(R.string.marker_text, time, value)

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