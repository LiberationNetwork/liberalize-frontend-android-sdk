package liberalize.kotlin.sdk.qr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import liberalize.kotlin.sdk.Liberalize
import liberalize.kotlin.sdk.databinding.LayoutQrCodeBinding
import liberalize.kotlin.sdk.models.QrCodeData


class QrCodeView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    style: Int = 0
) : ConstraintLayout(context, attributes, style) {

    companion object {
        private const val DEFAULT_WIDTH = 256
        private const val MARGIN_HORIZONTAL = 20
    }

    private val binding = LayoutQrCodeBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )
    private var qrCodeData: QrCodeData? = null

    private val webviewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressBar.hide()
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            binding.progressBar.hide()
        }
    }

    init {
        setWebviewSettings()
        hide()
    }

    /**
     * Configure webview
     */
    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun setWebviewSettings() {
        binding.webview.apply {
            setInitialScale(1)
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.javaScriptEnabled = true
            setOnTouchListener { _, event -> event.action == MotionEvent.ACTION_MOVE }
            webViewClient = webviewClient
        }
    }

    /**
     * render QR code
     * @param data
     */
    fun renderQrCode(data: QrCodeData) {
        show()
        qrCodeData = data
        val url = Liberalize.instance.getQrUrl(
            data.qrData,
            data.source,
            getSize()
        )
        binding.webview.loadUrl(url)
    }

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }

    /**
     * Calculate size of QR code
     */
    private fun getSize(): Int {
        var size: Int = if (height == 0) {
            width
        } else {
            val delta = width / height
            if (delta < 1) {
                width
            } else {
                height
            }
        }
        if (size == 0) size = DEFAULT_WIDTH
        return size
    }
}