package pl.sienczykm.templbn.ui.weather

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
import pl.sienczykm.templbn.background.WeatherUpdateWorker
import pl.sienczykm.templbn.utils.WeatherStation
import timber.log.Timber

class WeatherFragment : Fragment() {

    lateinit var weatherViewModel: WeatherViewModel
//    lateinit var binding: WeatherFragmentBinding

    companion object {
        fun newInstance(): WeatherFragment {
            val args = Bundle()
            val fragment = WeatherFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.weather_fragment, container, false)
//        binding = DataBindingUtil.inflate(inflater, R.layout.weather_fragment, container, false)
//        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherViewModel = activity?.run {
            ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance().enqueue(WeatherStation.STATIONS.map { weatherStation ->
            OneTimeWorkRequestBuilder<WeatherUpdateWorker>().setInputData(
                workDataOf(WeatherStation.ID_KEY to weatherStation.id)
            ).build()
        })

        weatherViewModel.getAllStations()
            .observe(this, Observer { stations -> stations.forEach { Timber.e(it.stationId.toString()) } })
    }

}