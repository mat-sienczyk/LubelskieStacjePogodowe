package pl.sienczykm.templbn.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.BR
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.background.StatusReceiver
import pl.sienczykm.templbn.utils.snackbarShow

abstract class BaseStationListFragment<K, T : BaseStationListViewModel<K>, N : ViewDataBinding> : Fragment() {


    lateinit var stationViewModel: T
    lateinit var binding: N

    abstract fun getViewModel(): T

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getSwipeToRefreshLayout(): SwipeRefreshLayout

    abstract fun getCoordinatorLayout(): CoordinatorLayout

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
            stationViewModel.refresh()
        }

        getSwipeToRefreshLayout().setColorSchemeColors(
            resources.getColor(R.color.main_yellow),
            resources.getColor(R.color.main_red),
            resources.getColor(R.color.main_green)
        )

        stationViewModel.status.observe(this, Observer { status -> handleStatus(status) })
    }

    private fun handleStatus(status: Int) {
        when (status) {
            StatusReceiver.STATUS_NO_CONNECTION -> handleError(R.string.no_connection)
            StatusReceiver.STATUS_ERROR -> handleError(R.string.error_server)
        }
    }

    private fun handleError(@StringRes message: Int) {
        snackbarShow(getCoordinatorLayout(), message)
    }

}