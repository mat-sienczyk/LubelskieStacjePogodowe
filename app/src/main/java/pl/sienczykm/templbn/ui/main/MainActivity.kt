package pl.sienczykm.templbn.ui.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.model.TempStationOne
import pl.sienczykm.templbn.model.TempStationTwo
import pl.sienczykm.templbn.remote.LspController
import pl.sienczykm.templbn.utils.Station
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.title_smog)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.title_temp)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.title_map)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val callbackOne = object : Callback<TempStationOne> {
        override fun onFailure(call: Call<TempStationOne>, t: Throwable) {
            Timber.e(t)
        }

        override fun onResponse(call: Call<TempStationOne>, response: Response<TempStationOne>) {
            if (response.isSuccessful) {
                Timber.d(response.body()?.windSpeed.toString())
            } else {
                Timber.e(response.message())
            }
        }
    }

    private val callbackTwo = object : Callback<TempStationTwo> {
        override fun onFailure(call: Call<TempStationTwo>, t: Throwable) {
            Timber.e(t)
        }

        override fun onResponse(call: Call<TempStationTwo>, response: Response<TempStationTwo>) {
            if (response.isSuccessful) {
                Timber.d(response.body()?.windSpeed.toString())
            } else {
                Timber.e(response.message())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        textMessage = findViewById(R.id.message)
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        LspController.getStationOne(Station.PLAC_LITEWSKI, callbackOne)
        LspController.getStationTwo(Station.LUKOW, callbackTwo)
    }
}
