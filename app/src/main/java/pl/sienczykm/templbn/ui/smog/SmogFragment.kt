package pl.sienczykm.templbn.ui.smog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.background.SmogUpdateWorker
import pl.sienczykm.templbn.utils.SmogStation
import timber.log.Timber

class SmogFragment : Fragment() {

    lateinit var smogViewModel: SmogViewModel
//    lateinit var binding: WeatherFragmentBinding

    companion object {
        fun newInstance(): SmogFragment {
            val args = Bundle()
            val fragment = SmogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.smog_fragment, container, false)
//        binding = DataBindingUtil.inflate(inflater, R.layout.weather_fragment, container, false)
//        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smogViewModel = activity?.run {
            ViewModelProviders.of(this).get(SmogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance().enqueue(SmogStation.STATIONS.map { smogStation ->
            OneTimeWorkRequestBuilder<SmogUpdateWorker>().setInputData(
                workDataOf(SmogStation.ID_KEY to smogStation.id)
            ).build()
        })

        smogViewModel.getAllStations()
            .observe(this, Observer { stations -> stations.forEach { Timber.e(it.stationId.toString()) } })
    }

}