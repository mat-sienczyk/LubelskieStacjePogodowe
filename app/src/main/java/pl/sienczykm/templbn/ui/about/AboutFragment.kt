package pl.sienczykm.templbn.ui.about

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.sienczykm.templbn.R
import pl.sienczykm.templbn.databinding.FragmentAboutBinding
import pl.sienczykm.templbn.utils.getColorHex
import pl.sienczykm.templbn.utils.openUrl
import pl.sienczykm.templbn.utils.snackbarShow
import timber.log.Timber
import java.util.*

class AboutFragment : Fragment(), AboutNavigator {
    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    private lateinit var aboutViewModel: AboutViewModel
    private lateinit var binding: FragmentAboutBinding
    private var dialog: AlertDialog? = null

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

    override fun onDestroyView() {
        dialog?.dismiss()
        super.onDestroyView()
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun openDialog(dialogType: DialogType) {
        val webView = WebView(requireContext().applicationContext).apply {
            setBackgroundColor(Color.TRANSPARENT)
            settings.javaScriptEnabled = true
            val hexColor = requireContext().getColorHex(R.color.base_color)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.loadUrl("javascript:document.body.style.setProperty(\"color\", \"$hexColor\");")
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    return requireContext().openUrl(request?.url.toString())
                }
            }
            loadUrl("file:///android_asset/${dialogType.name.toLowerCase(Locale.ENGLISH)}.html")
        }

        dialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(dialogType.title)
            setPositiveButton(R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
            setView(webView)
        }.create().apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }

        dialog?.show()
    }

    enum class DialogType(@StringRes val title: Int) {
        TERMS(R.string.terms), LICENSE(R.string.license)

    }
}