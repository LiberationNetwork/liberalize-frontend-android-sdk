package liberalize.kotlin.sdk.network

import liberalize.kotlin.sdk.models.CardDetailsResponse
import liberalize.kotlin.sdk.models.CardRequest

interface LiberalizeAPIClient {
    fun mountCard(
        request: CardRequest,
        onSuccess: (CardDetailsResponse?) -> Unit,
        onError: (String?) -> Unit
    )
}