package pl.sienczykm.templbn.ui.smog

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.SmogFragmentBinding
import pl.sienczykm.templbn.db.model.SmogStationModel
import pl.sienczykm.templbn.ui.common.BaseStationListFragment
import timber.log.Timber

class SmogFragment : BaseStationListFragment<SmogStationModel, SmogViewModel, SmogFragmentBinding>() {

    companion object {
        fun newInstance(): SmogFragment {
            val args = Bundle()
            val fragment = SmogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(): SmogViewModel {
        return activity?.run {
            ViewModelProviders.of(this).get(SmogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.smog_fragment
    }

    override fun getSwipeToRefreshLayout(): SwipeRefreshLayout {
        return binding.swipeLayout
    }

    override fun getCoordinatorLayout(): CoordinatorLayout {
        return binding.coordinatorLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stationViewModel.getAllStations()
            .observe(
                this,
                Observer { stations ->
                    Timber.e(stations.joinToString(
                        separator = "\n",
                        transform = { "Nazwa: " + it.name + ", nr stacji: " + it.stationId + ", data: " + it.date }))
                })
    }
}