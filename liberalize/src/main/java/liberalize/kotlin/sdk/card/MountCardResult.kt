package liberalize.kotlin.sdk.card

import liberalize.kotlin.sdk.models.MountCardData

interface MountCardResult {
    fun onSuccess(cardResponse: MountCardData?)
    fun onError(msg: String?)
}