package pl.sienczykm.templbn.ui.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.model.TempStation
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

    private val callback = object : Callback<TempStation> {
        override fun onFailure(call: Call<TempStation>, t: Throwable) {
            Timber.e(t)
        }

        override fun onResponse(call: Call<TempStation>, response: Response<TempStation>) {
            if (response.isSuccessful) {
                (response.body() as TempStation).toString()
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

        LspController.getStation(Station.PLAC_LITEWSKI, callback)
        LspController.getStation(Station.LUKOW, callback)
    }
}
