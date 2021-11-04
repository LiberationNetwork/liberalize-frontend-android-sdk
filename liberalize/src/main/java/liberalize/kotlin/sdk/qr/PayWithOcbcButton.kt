package liberalize.kotlin.sdk.qr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import liberalize.kotlin.sdk.Liberalize
import liberalize.kotlin.sdk.models.QrCodeData
import liberalize.kotlin.sdk.logger.Logger

class PayWithOcbcButton @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    style: Int = 0
) : AppCompatButton(context, attributes, style) {

    init {
        visibility = GONE
        setOnClickListener {
            if (qrCodeData?.isOcbc == true) {
                startOcbcApp()
            }
        }
    }

    /**
     * need to set QRCodeData first
     */
    var qrCodeData: QrCodeData? = null
        set(value) {
            field = value
            // show only for Paynow
            visibility = if (value?.isOcbc == true) VISIBLE else GONE
        }

    /**
     * Open OCBC deeplink
     */
    private fun startOcbcApp() {
        qrCodeData?.let {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val link = Liberalize.instance.getOcbcDeeplink(context, it)
                intent.data = Uri.parse(link)
                context.startActivity(intent)
            } catch (e: Exception) {
                Logger.e("Liberalize", "OCBC APP Not Found")
            }
        }
    }
}
