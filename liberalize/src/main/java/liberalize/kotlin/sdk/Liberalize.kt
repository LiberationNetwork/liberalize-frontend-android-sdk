package liberalize.kotlin.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import liberalize.kotlin.sdk.card.CardDetailsActivity
import liberalize.kotlin.sdk.card.MountCardResult
import liberalize.kotlin.sdk.logger.Logger
import liberalize.kotlin.sdk.models.MountCardData
import liberalize.kotlin.sdk.models.QrCodeData
import liberalize.kotlin.sdk.network.*
import liberalize.kotlin.sdk.utils.base64

class Liberalize private constructor(
    private val publicKey: String, env: Env
) {

    companion object {
        private const val TAG = "Liberalize"

        @Volatile
        private var INSTANCE: Liberalize? = null

        /**
         * Debug mode
         */
        @JvmStatic
        var enabledDebug: Boolean = false
            set(value) {
                field = value
                Logger.isLogsRequired = value
            }

        @JvmStatic
        val instance: Liberalize
            get() {
                if (INSTANCE == null) throw Exception("You must initialize Liberalize first")
                return INSTANCE!!
            }

        /**
         * Initialize
         */
        @JvmStatic
        fun init(publicKey: String, env: Env): Liberalize {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Liberalize("$publicKey:".base64, env).also {
                    INSTANCE = it.apply {
                        createApiClient()
                    }
                }
            }
        }
    }

    /**
     * Environment
     * DEVELOPMENT
     * STAGING
     * PRODUCTION
     */
    enum class Env {
        DEVELOPMENT, STAGING, PRODUCTION
    }

    /**
     * Card base url
     */
    private val cardUrl: String = when (env) {
        Env.DEVELOPMENT -> RemoteContract.BaseUrl.DEVELOPMENT_CARD
        Env.STAGING -> RemoteContract.BaseUrl.STAGING_CARD
        Env.PRODUCTION -> RemoteContract.BaseUrl.PRODUCTION_CARD
    }

    /**
     * QR base url
     */
    private val qrUrl: String = when (env) {
        Env.DEVELOPMENT -> RemoteContract.BaseUrl.DEVELOPMENT_QR
        Env.STAGING -> RemoteContract.BaseUrl.STAGING_QR
        Env.PRODUCTION -> RemoteContract.BaseUrl.PRODUCTION_QR
    }

    /**
     * Activity Result Launcher
     */
    private var launcher: ActivityResultLauncher<Intent>? = null

    /**
     * Liberalize API client
     */
    @set:JvmSynthetic
    @get:JvmSynthetic
    internal var apiClient: LiberalizeAPIClient? = null

    /**
     * Get OCBC deeplink
     * This func is only used in library only
     * @param qrData QR string
     * @param source QR payment source
     * @param size Size of the QR Code
     */
    @JvmSynthetic
    internal fun getQrUrl(qrData: String, source: String, size: Int): String {
        return "$qrUrl?qrData=$qrData&source=$source&size=$size"
    }

    /**
     * Get OCBC deeplink
     * This func is only used in library only
     * @param context
     * @param qrCodeData QrCodeData
     */
    @JvmSynthetic
    internal fun getOcbcDeeplink(context: Context, qrCodeData: QrCodeData): String {
        val applicationId = context.packageName
        val encodedQrString = Uri.encode(qrCodeData.qrData, UTF_8)
        val stringBuilder = StringBuilder("ocbcpao://readQR?version=3&qrString=").apply {
            append(encodedQrString)
            append("&transactionID=")
            append(qrCodeData.paymentId)
            append("&appID=")
            append(applicationId)
            append("&intentAction=android.intent.action.VIEW&returnURI=")
            append(applicationId)
            append("://action&shouldOpenInExternalBrowser=true")
        }
        return stringBuilder.toString()
    }

    /**
     * Use this func to mount card
     */
    fun mountCard(context: Context) {
        launcher?.launch(Intent(context, CardDetailsActivity::class.java))
    }

    /**
     * Register card result with Fragment, must call this at onCreate()
     * @param fragment fragment to register
     * @param onResult callback
     */
    fun registerCardResult(
        fragment: Fragment,
        onResult: MountCardResult
    ) {
        launcher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            getCardResult(it, onResult)
        }
    }

    /**
     * Register card result with AppCompatActivity, must call this at onCreate()
     * @param activity activity to register
     * @param onResult callback
     */
    fun registerCardResult(
        activity: AppCompatActivity,
        onResult: MountCardResult
    ) {
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            getCardResult(it, onResult)
        }
    }

    /**
     * Handle data back from CardDetailsActivity
     */
    private fun getCardResult(activityResult: ActivityResult, onResult: MountCardResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = activityResult.data?.getParcelableExtra<MountCardData>(
                CardDetailsActivity.CARD_RESULT_EXTRA
            )
            onResult.onSuccess(result)
        }
        if (activityResult.resultCode == Activity.RESULT_CANCELED) {
            val err = activityResult.data?.getStringExtra(
                CardDetailsActivity.CARD_ERROR_EXTRA
            )
            onResult.onError(err)
        }
    }

    /**
     * Create API Client for library
     */
    private fun createApiClient() {
        if (publicKey.isEmpty()) {
            Logger.e(TAG, "public key is empty, please correct")
            return
        }
        val httpClient = LiberalizeHttpClientImpl(
            url = cardUrl,
            publicKey = publicKey
        )
        Logger.d(TAG, "public key $publicKey loaded successfully")
        apiClient = LiberalizeAPIClientImpl(httpClient)
    }
}

