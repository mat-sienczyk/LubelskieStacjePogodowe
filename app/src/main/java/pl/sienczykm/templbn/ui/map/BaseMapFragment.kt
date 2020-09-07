package pl.sienczykm.templbn.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.db.model.BaseStationModel
import pl.sienczykm.templbn.db.model.WeatherStationModel
import pl.sienczykm.templbn.ui.station.StationActivity

abstract class BaseMapFragment<T : ViewDataBinding> : Fragment(), MapNavigator {

    lateinit var binding: T

    var stations: List<BaseStationModel>? = null

    lateinit var viewModel: MapViewModel

    private var filterDialog: AlertDialog? = null

    abstract fun configureMap()

    abstract override fun updateMap()

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
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
        viewModel.stations.observe(viewLifecycleOwner, { stations ->
            this.stations = stations
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

    override fun openFilters() {
        filterDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.filters)
            setPositiveButton(R.string.save) { dialogInterface, _ ->
                viewModel.setFilters(WeatherFilter.TEMPERATURE, AirFilter.NOTHING)
                dialogInterface.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                viewModel.setFilters(WeatherFilter.LOCATION, AirFilter.LOCATION)
                dialogInterface.dismiss()
            }
//            setView(webView) // TODO create, inflate view, whateva
        }.create().apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }

        filterDialog?.show()
    }
}