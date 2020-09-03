package pl.sienczykm.templbn.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.db.model.BaseStationModel

abstract class BaseMapFragment<T : ViewDataBinding> : Fragment(), MapNavigator {

    lateinit var binding: T

    var stations: List<BaseStationModel>? = null

    private lateinit var viewModel: MapViewModel

    abstract fun configureMap()

    abstract fun updateMap()

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
        savedInstanceState: Bundle?
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


}