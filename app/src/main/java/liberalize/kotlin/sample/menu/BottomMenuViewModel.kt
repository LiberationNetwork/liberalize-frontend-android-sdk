package liberalize.kotlin.sample.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import liberalize.kotlin.sample.BuildConfig
import liberalize.kotlin.sample.data.PaymentProvider
import liberalize.kotlin.sample.data.RemoteContract
import liberalize.kotlin.sample.utils.base64
import org.json.JSONObject


class BottomMenuViewModel : ViewModel() {

    private val qrProviders: MutableLiveData<List<PaymentProvider>> by lazy {
        MutableLiveData<List<PaymentProvider>>().also {
            loadQrProviders()
        }
    }

    val card = MutableLiveData<PaymentProvider>()

    val isLoading = MutableLiveData(true)

    fun getQrProviders(): LiveData<List<PaymentProvider>> {
        return qrProviders
    }

    /**
     * Get base url by
     */
    private fun getBaseUrl(): String {
        return if (BuildConfig.FLAVOR == "staging" || BuildConfig.FLAVOR == "dev") {
            RemoteContract.STAGING_BASE_URL
        } else {
            RemoteContract.PRODUCTION_BASE_URL
        }
    }

    /**
     * Get supported payment using public API key
     */
    private fun loadQrProviders() {
        val header = mapOf("Authorization" to "Basic " + "<public key>:".base64)
        AndroidNetworking.get("${getBaseUrl()}supported")
            .addHeaders(header)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    val iterator = response?.keys()
                    val list = mutableListOf<PaymentProvider>()
                    while (iterator?.hasNext() == true) {
                        val key = iterator.next()
                        val data = response.optJSONArray(key)?.get(0)
                        data.let {
                            list.add(Gson().fromJson(it.toString(), PaymentProvider::class.java))
                        }
                    }
                    card.value = list.find { it.source == "card" }
                    list.remove(card.value)
                    qrProviders.value = list
                    isLoading.value = false
                }

                override fun onError(anError: ANError?) {
                    Log.e("loadQrProviders", "$anError")
                    isLoading.value = false
                }
            })
    }
}