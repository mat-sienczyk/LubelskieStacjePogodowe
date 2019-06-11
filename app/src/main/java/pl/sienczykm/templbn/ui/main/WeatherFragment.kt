package pl.sienczykm.templbn.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.sienczykm.templbn.background.WeatherUpdateWorker
import pl.sienczykm.templbn.utils.WeatherStation

class WeatherFragment : Fragment() {

    companion object {
        fun newInstance(): WeatherFragment {
            val args = Bundle()
            val fragment = WeatherFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance().enqueue(WeatherStation.STATIONS.map { weatherStation ->
            OneTimeWorkRequestBuilder<WeatherUpdateWorker>().setInputData(
                workDataOf(WeatherStation.ID_KEY to weatherStation.id)
            ).build()
        })
    }

}