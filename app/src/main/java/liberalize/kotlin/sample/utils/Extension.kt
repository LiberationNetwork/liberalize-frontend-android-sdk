package liberalize.kotlin.sample.utils

import android.util.Base64

internal val String.base64: String
    get() = Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP)
