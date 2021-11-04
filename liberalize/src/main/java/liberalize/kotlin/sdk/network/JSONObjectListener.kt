package liberalize.kotlin.sdk.network

import org.json.JSONObject

internal interface JSONObjectListener {
    fun onResponse(res: JSONObject?)
    fun onFailure(e: Exception?)
}