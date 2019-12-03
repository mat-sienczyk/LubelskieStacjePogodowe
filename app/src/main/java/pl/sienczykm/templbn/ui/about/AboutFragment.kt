package pl.sienczykm.templbn.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentAboutBinding
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber

class AboutFragment : Fragment(), AboutNavigator {
    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    private lateinit var aboutViewModel: AboutViewModel
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        binding.viewModel = aboutViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aboutViewModel = ViewModelProvider(requireActivity()).get(AboutViewModel::class.java)
        aboutViewModel.setNavigator(this)
    }

    override fun openGooglePlay(appId: String) {
        val rateAppIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=$appId")
        }

        try {
            startActivity(rateAppIntent)
        } catch (e: Exception) {
            Timber.e("Error with Google Play: ${e.localizedMessage}")
            binding.coordinatorLayout.snackbarShow(R.string.error_play_store)
        }
    }

    override fun openDialog(dialogType: DialogType) {
        Timber.d(dialogType.name)
    }

    enum class DialogType {
        TERMS, LICENSE
    }
}