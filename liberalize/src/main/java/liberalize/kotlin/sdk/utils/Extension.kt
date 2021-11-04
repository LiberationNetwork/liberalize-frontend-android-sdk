package liberalize.kotlin.sdk.utils

import android.content.res.Resources
import android.util.Base64

internal val String.base64: String
    get() = Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP)

internal val Int.px2dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

internal val Int.dp2px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()