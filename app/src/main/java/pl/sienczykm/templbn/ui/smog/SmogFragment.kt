package pl.sienczykm.templbn.ui.smog

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.SmogFragmentBinding
import pl.sienczykm.templbn.db.model.SmogStationDb
import pl.sienczykm.templbn.ui.common.BaseStationListFragment

class SmogFragment : BaseStationListFragment<SmogStationDb, SmogViewModel, SmogFragmentBinding>() {

    override fun getViewModel(): SmogViewModel {
        return activity?.run {
            ViewModelProviders.of(this).get(SmogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun getLayoutId(): Int {
        return R.layout.smog_fragment
    }

    companion object {
        fun newInstance(): SmogFragment {
            val args = Bundle()
            val fragment = SmogFragment()
            fragment.arguments = args
            return fragment
        }
    }

}