package liberalize.kotlin.sdk.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

data class CardRequest(
    val name: String?,
    val securityCode: String?,
    val number: String,
    val expiry: Expiry
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("type", "card")
            put(
                "card",
                JSONObject(
                    mapOf(
                        "name" to name,
                        "number" to number,
                        "expiry" to expiry.toJson(),
                    )
                )
            )
        }
    }
}

@Parcelize
data class Expiry(
    val month: String?,
    val year: String?
) : Parcelable {
    fun toJson(): JSONObject {
        return JSONObject(mapOf("month" to month, "year" to year))
    }

    companion object {
        fun fromJson(res: JSONObject?): Expiry {
            val month = if (res?.has("month") == true) res.get("month") as String? else null
            val year = if (res?.has("year") == true) res.get("year") as String? else null
            return Expiry(month, year)
        }
    }
}