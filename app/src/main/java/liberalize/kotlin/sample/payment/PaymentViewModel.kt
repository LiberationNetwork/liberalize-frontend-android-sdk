package liberalize.kotlin.sample.payment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import liberalize.java.backend.sdk.LiberalizeBE
import liberalize.java.backend.sdk.data.model.CapturePaymentRequest
import liberalize.java.backend.sdk.data.model.CreatePaymentRequest
import liberalize.java.backend.sdk.data.model.PaymentDetailsResponse
import liberalize.java.backend.sdk.data.model.PaymentFlowResponse
import liberalize.java.backend.sdk.data.network.ApiResult

class PaymentViewModel : ViewModel() {

    val qrData = MutableLiveData<ResponseDataWrapper>()
    val isLoading = MutableLiveData(false)
    val cardPaymentSuccess = MutableLiveData<String?>()
    val qrPaymentSuccess = MutableLiveData<String?>()

    /**
     * Create payment using BE SDK
     * @param source QR source
     */
    fun createPayment(source: String) {
        isLoading.value = true
        LiberalizeBE.getInstance().createPayment(
            CreatePaymentRequest(
                source,
                100,
                true,
                "SGD"
            ),
            "elements",
            object : ApiResult<PaymentFlowResponse> {
                override fun onError(p0: String?) {
                    Log.e("createPayment err", "$p0")
                    isLoading.postValue(false)
                }

                override fun onSuccess(response: PaymentFlowResponse?) {
                    Log.d("createPayment success", "${response?.paymentId}")
                    isLoading.postValue(false)
                    qrData.postValue(
                        ResponseDataWrapper(
                            source,
                            response?.processor?.qrData,
                            response?.paymentId
                        )
                    )
                }
            }
        )
    }

    /**
     * Create payment using BE SDK
     * @param source card source
     */
    fun createCardPayment(source: String) {
        isLoading.value = true
        LiberalizeBE.getInstance().createPayment(
            CreatePaymentRequest(
                source,
                100,
                true,
                "SGD"
            ),
            "elements",
            object : ApiResult<PaymentFlowResponse> {
                override fun onError(p0: String?) {
                    Log.e("createPayment err", "$p0")
                    isLoading.postValue(false)
                }

                override fun onSuccess(response: PaymentFlowResponse?) {
                    Log.d("createCardPayment success", "${response?.paymentId}")
                    isLoading.postValue(false)
                    capturePayment(response?.paymentId)
                }
            }
        )
    }

    /**
     * Capture payment using BE SDK
     * @param paymentId payment id after created
     */
    fun capturePayment(paymentId: String?) {
        isLoading.postValue(true)
        LiberalizeBE.getInstance().capturePayment(
            CapturePaymentRequest(100),
            "elements",
            paymentId,
            object : ApiResult<PaymentFlowResponse> {
                override fun onError(p0: String?) {
                    Log.e("capturePayment err", "$p0")
                    isLoading.postValue(false)
                }

                override fun onSuccess(data: PaymentFlowResponse?) {
                    Log.d("capturePayment success", "${data?.paymentId}")
                    isLoading.postValue(false)
                    cardPaymentSuccess.postValue(data?.paymentId)
                }
            }
        )
    }

    /**
     * Get payment details using BE SDK
     * @param paymentId payment id
     */
    fun pollPayment(paymentId: String?) {
        isLoading.value = true
        LiberalizeBE.getInstance().getPayment(
            "elements",
            paymentId,
            object : ApiResult<PaymentDetailsResponse> {
                override fun onError(p0: String?) {
                    Log.e("pollPayment err", "$p0")
                    isLoading.postValue(false)
                }

                override fun onSuccess(data: PaymentDetailsResponse?) {
                    isLoading.postValue(false)
                    Log.d("pollPayment success", "${data?.state}")
                    if (data?.state == "SUCCESS") {
                        // send success value
                        qrPaymentSuccess.postValue(data.id)
                    }
                }
            }
        )
    }

    data class ResponseDataWrapper(
        @JvmField
        val source: String,
        @JvmField
        val qrData: String?,
        @JvmField
        val paymentId: String?,
    )
}