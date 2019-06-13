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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.BR
import timber.log.Timber

abstract class BaseStationListFragment<K, T : BaseStationListViewModel<K>, N : ViewDataBinding> : Fragment() {


    lateinit var stationViewModel: T
    lateinit var binding: N

    abstract fun getViewModel(): T

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getSwipeToRefreshLayout(): SwipeRefreshLayout

    abstract fun updateJob()

    fun refresh() {
        getSwipeToRefreshLayout().isRefreshing = true
        updateJob()
        getSwipeToRefreshLayout().isRefreshing = false
    }

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

        getSwipeToRefreshLayout().setOnRefreshListener {
            refresh()
        }
//        getSwipeToRefreshLayout().setColorSchemeColors(getColor(R.color.main_yellow), getResources().getColor(R.color.main_red), getResources().getColor(R.color.main_green));

        stationViewModel.getAllStations()
            .observe(this, Observer { stations -> Timber.e("Number of stations: %s", stations.size.toString()) })


        refresh()
    }

}