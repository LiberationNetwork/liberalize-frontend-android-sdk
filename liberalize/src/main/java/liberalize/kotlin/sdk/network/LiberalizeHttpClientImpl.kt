package liberalize.kotlin.sdk.network

import liberalize.kotlin.sdk.logger.Logger
import org.json.JSONObject

internal class LiberalizeHttpClientImpl(
    private val url: String,
    private val publicKey: String
) : LiberalizeHttpClient {

    companion object {
        private const val TAG = "LiberalizeHttpClient"
        private const val HEADER_AUTH = "Authorization"
    }

    override fun get(
        endpoint: String,
        headers: Map<String, String>?,
        onSuccess: (JSONObject?) -> Unit,
        onError: (Exception?) -> Unit
    ) {
        getHttpRequest(endpoint, Http.GET, headers).execute(object : JSONObjectListener {
            override fun onResponse(res: JSONObject?) {
                Logger.d(TAG, "${res?.toString(2)}")
                onSuccess.invoke(res)
            }

            override fun onFailure(e: Exception?) {
                Logger.d(TAG, "${e?.message}")
                onError.invoke(e)
            }
        })
    }

    override fun post(
        endpoint: String,
        headers: Map<String, String>?,
        body: JSONObject,
        onSuccess: (JSONObject?) -> Unit,
        onError: (Exception?) -> Unit
    ) {
        getHttpRequest(endpoint, Http.POST, headers, body).execute(object : JSONObjectListener {
            override fun onResponse(res: JSONObject?) {
                Logger.d(TAG, "${res?.toString(2)}")
                onSuccess.invoke(res)
            }

            override fun onFailure(e: Exception?) {
                Logger.d(TAG, "${e?.message}")
                onError.invoke(e)
            }
        })
    }

    override fun put(
        endpoint: String,
        headers: Map<String, String>?,
        body: JSONObject,
        onSuccess: (JSONObject?) -> Unit,
        onError: (Exception?) -> Unit
    ) {
        getHttpRequest(endpoint, Http.PUT, headers, body).execute(object : JSONObjectListener {
            override fun onResponse(res: JSONObject?) {
                Logger.d(TAG, "${res?.toString(2)}")
                onSuccess.invoke(res)
            }

            override fun onFailure(e: Exception?) {
                Logger.d(TAG, "${e?.message}")
                onError.invoke(e)
            }
        })
    }

    private fun getHttpRequest(
        endpoint: String,
        method: String,
        headers: Map<String, String>?,
        body: JSONObject? = null
    ): Http.Request {
        return Http.Request(method).apply {
            uri = url + endpoint
            header(HEADER_AUTH, "Basic $publicKey")
            header(headers)
            body?.let { body(it) }
        }
    }
}