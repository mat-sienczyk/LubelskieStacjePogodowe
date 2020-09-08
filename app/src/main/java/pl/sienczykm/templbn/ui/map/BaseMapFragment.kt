package pl.sienczykm.templbn.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.filters_spinner.view.*
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.station.StationActivity
import pl.sienczykm.templbn.utils.getAirFilter
import pl.sienczykm.templbn.utils.getWeatherFilter
import pl.sienczykm.templbn.utils.setAirFilter
import pl.sienczykm.templbn.utils.setWeatherFilter


abstract class BaseMapFragment<T : ViewDataBinding> : Fragment(), MapNavigator {

    lateinit var binding: T

    lateinit var viewModel: MapViewModel

    private var filterDialog: AlertDialog? = null

    abstract fun configureMap()

    abstract override fun updateMap()

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            MapViewViewModelFactory(
                requireActivity().application,
                requireActivity().getWeatherFilter(),
                requireActivity().getAirFilter(),
            )
        ).get(MapViewModel::class.java)
        viewModel.setNavigator(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = viewLifecycleOwner

        configureMap()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stations.observe(viewLifecycleOwner, {
            updateMap()
        })
    }

    override fun onDestroyView() {
        filterDialog?.dismiss()
        super.onDestroyView()
    }

    fun openStation(station: BaseStationModel) {
        startActivity(Intent(requireContext(), StationActivity::class.java).apply {
            putExtra(
                StationActivity.STATION_TYPE_KEY,
                if (station is WeatherStationModel) StationActivity.Type.WEATHER else StationActivity.Type.AIR
            )
            putExtra(StationActivity.STATION_ID_KEY, station.stationId)
        })
    }

    @SuppressLint("InflateParams")
    override fun openFilters() {
        filterDialog = AlertDialog.Builder(requireContext()).apply {

            var activeWeatherFilter = requireContext().getWeatherFilter()
            var activeAirFilter = requireContext().getAirFilter()

            val dialogView = layoutInflater.inflate(R.layout.filters_spinner, null)
            dialogView.weatherFilters.apply {
                populateSpinnerWithFilters(activeWeatherFilter,
                    WeatherFilter.values().toList()) {
                    activeWeatherFilter = it
                }
            }
            dialogView.airFilters.apply {
                populateSpinnerWithFilters(activeAirFilter,
                    AirFilter.values().toList()) {
                    activeAirFilter = it
                }
            }

            setTitle(R.string.filters)
            setPositiveButton(R.string.save) { dialogInterface, _ ->
                requireContext().setWeatherFilter(activeWeatherFilter)
                requireContext().setAirFilter(activeAirFilter)
                viewModel.setFilters(activeWeatherFilter, activeAirFilter)
                dialogInterface.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            setView(dialogView)
        }.create().apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }

        filterDialog?.show()
    }

    private fun <T : StringIdRes> Spinner.populateSpinnerWithFilters(
        activeFilter: T,
        filterList: List<T>,
        onItemSelected: (T) -> Unit,
    ) {
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            filterList.map { getString(it.getStringId()) })
        setSelection(filterList.indexOf(activeFilter))
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                onItemSelected(filterList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }
    }
}