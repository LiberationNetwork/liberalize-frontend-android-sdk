package liberalize.kotlin.sdk.network

import liberalize.kotlin.sdk.models.CardDetailsResponse
import liberalize.kotlin.sdk.models.CardRequest
import liberalize.kotlin.sdk.models.ErrorResponse

internal class LiberalizeAPIClientImpl(
    private val httpClient: LiberalizeHttpClient
) : LiberalizeAPIClient {

    override fun mountCard(
        request: CardRequest,
        onSuccess: (CardDetailsResponse?) -> Unit,
        onError: (String?) -> Unit
    ) {
        httpClient.post(
            endpoint = "paymentMethods",
            headers = mapOf("x-lib-pos-type" to "elements"),
            body = request.toJson(),
            onSuccess = { jsonObject ->
                val errorRes = ErrorResponse.fromJson(jsonObject)
                errorRes?.let {
                    onError.invoke(it.message)
                    return@let
                }
                val result =
                    if (jsonObject == null) null
                    else CardDetailsResponse.fromJson(jsonObject)
                onSuccess.invoke(result)
            },
            onError = {
                onError.invoke(it?.message)
            }
        )
    }
}