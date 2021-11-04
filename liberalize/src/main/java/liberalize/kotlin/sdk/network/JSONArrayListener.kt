package liberalize.kotlin.sdk.network

import org.json.JSONArray

internal interface JSONArrayListener {
    fun onResponse(res: JSONArray?)
    fun onFailure(e: Exception?)
}