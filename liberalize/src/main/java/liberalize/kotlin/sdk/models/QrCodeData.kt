package liberalize.kotlin.sdk.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class QrCodeData(
    @JvmField
    val qrData: String,
    @JvmField
    val source: String,
    @JvmField
    val paymentId: String? = null
): Parcelable {
    companion object {
        private const val OCBC_SOURCE = "paynow"
    }

    val isOcbc: Boolean
        get() = source == OCBC_SOURCE
}