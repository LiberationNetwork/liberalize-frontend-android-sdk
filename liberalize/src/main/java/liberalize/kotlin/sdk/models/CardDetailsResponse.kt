package liberalize.kotlin.sdk.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class MountCardData(
    @JvmField
    val source: String?,
    @JvmField
    val securityCode: String?
) : Parcelable

@Parcelize
data class CardDetailsResponse(
    @JvmField
    val id: String?,
    @JvmField
    val posId: String?,
    @JvmField
    val organizationId: String?,
    @JvmField
    val card: Card?,
    @JvmField
    var securityCode: String? = null
) : Parcelable {
    companion object {
        fun fromJson(res: JSONObject?): CardDetailsResponse {
            val id = if (res?.has("id") == true) res.get("id") as String? else null
            val posId = if (res?.has("posId") == true) res.get("posId") as String? else null
            val organizationId =
                if (res?.has("organizationId") == true) {
                    res.get("organizationId") as String?
                } else {
                    null
                }
            val card =
                if (res?.has("card") == true) {
                    Card.fromJson(res.get("card") as JSONObject?)
                } else {
                    null
                }
            return CardDetailsResponse(
                id = id,
                posId = posId,
                organizationId = organizationId,
                card = card
            )
        }
    }
}

@Parcelize
data class Card(
    val name: String?,
    val data: CardData?,
    val expiry: Expiry?
) : Parcelable {
    companion object {
        fun fromJson(res: JSONObject?): Card {
            val name = if (res?.has("name") == true) res.get("name") as String? else null
            val cardData =
                if (res?.has("data") == true) {
                    CardData.fromJson(res.get("data") as JSONObject?)
                } else {
                    null
                }
            val expiry =
                if (res?.has("expiry") == true) {
                    Expiry.fromJson(res.get("expiry") as JSONObject?)
                } else {
                    null
                }
            return Card(name, cardData, expiry)
        }
    }
}

@Parcelize
data class CardData(
    val iin: String?,
    val last4: String?,
    val scheme: String?
) : Parcelable {
    companion object {
        fun fromJson(res: JSONObject?): CardData {
            val iin = if (res?.has("iin") == true) res.get("iin") as String? else null
            val last4 = if (res?.has("last4") == true) res.get("last4") as String? else null
            val scheme = if (res?.has("scheme") == true) res.get("scheme") as String? else null
            return CardData(iin, last4, scheme)
        }
    }
}

@Parcelize
class ErrorResponse(
    val code: Int?,
    var message: String?
) : Parcelable {
    companion object {
        fun fromJson(res: JSONObject?): ErrorResponse? {
            val code = if (res?.has("code") == true) res.get("code") as Int? else null
            val msg = if (res?.has("message") == true) res.get("message") as String? else null
            return if (code == null && msg == null) null else ErrorResponse(code, msg)
        }
    }
}
