package pl.sienczykm.templbn.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import pl.sienczykm.templbn.BR
import timber.log.Timber

abstract class BaseStationListFragment<K, T : BaseStationListViewModel<K>, N : ViewDataBinding> : Fragment() {


    lateinit var stationViewModel: T
    lateinit var binding: N

    abstract fun getViewModel(): T

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun refresh()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.setVariable(BR.viewModel, stationViewModel)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stationViewModel = getViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh()

        stationViewModel.getAllStations()
            .observe(this, Observer { stations -> Timber.e("Number of stations: %s", stations.size.toString()) })
    }

}