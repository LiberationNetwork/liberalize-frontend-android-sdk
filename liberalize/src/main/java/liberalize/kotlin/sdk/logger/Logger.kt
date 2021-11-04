package liberalize.kotlin.sdk.logger

import android.util.Log

internal object Logger {
    var isLogsRequired = false
    fun d(tag: String?, message: String?) {
        if (isLogsRequired) Log.d(tag, message.orEmpty())
    }

    fun e(tag: String?, message: String?) {
        if (isLogsRequired) Log.e(tag, message.orEmpty())
    }
}