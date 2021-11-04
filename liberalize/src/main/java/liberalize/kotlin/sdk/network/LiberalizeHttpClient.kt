package liberalize.kotlin.sdk.network

import org.json.JSONObject

internal interface LiberalizeHttpClient {
    fun get(
        endpoint: String,
        headers: Map<String, String>? = null,
        onSuccess: (JSONObject?) -> Unit,
        onError: (Exception?) -> Unit
    )

    fun post(
        endpoint: String,
        headers: Map<String, String>? = null,
        body: JSONObject,
        onSuccess: (JSONObject?) -> Unit,
        onError: (Exception?) -> Unit
    )

    fun put(
        endpoint: String,
        headers: Map<String, String>? = null,
        body: JSONObject,
        onSuccess: (JSONObject?) -> Unit,
        onError: (Exception?) -> Unit
    )
}