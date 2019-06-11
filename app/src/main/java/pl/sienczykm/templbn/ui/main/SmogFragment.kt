package pl.sienczykm.templbn.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.sienczykm.templbn.background.SmogUpdateWorker
import pl.sienczykm.templbn.utils.SmogStation

class SmogFragment : Fragment() {

    companion object {
        fun newInstance(): SmogFragment {
            val args = Bundle()
            val fragment = SmogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance().enqueue(SmogStation.STATIONS.map { smogStation ->
            OneTimeWorkRequestBuilder<SmogUpdateWorker>().setInputData(
                workDataOf(SmogStation.ID_KEY to smogStation.id)
            ).build()
        })
    }

}