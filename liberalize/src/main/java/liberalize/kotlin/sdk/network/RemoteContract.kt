package liberalize.kotlin.sdk.network

internal object RemoteContract {
    object BaseUrl {
        const val PRODUCTION_CARD = "https://customer.api.liberalize.io/"
        const val STAGING_CARD = "https://customer.api.staging.liberalize.io/"
        const val DEVELOPMENT_CARD = "https://customer.api.dev.liberalize.io/"

        const val PRODUCTION_QR = "https://qr-element.liberalize.io/#/"
        const val STAGING_QR = "https://qr-element.staging.liberalize.io/#/"
        const val DEVELOPMENT_QR = "https://qr-element.dev.liberalize.io/#/"
    }
}