package liberalize.kotlin.sdk.network

import liberalize.kotlin.sdk.logger.Logger
import liberalize.kotlin.sdk.network.ProgressListener.ResponseListener

internal class DownloadProgressManager internal constructor(
    private val progressListener: ProgressListener,
    private val progressCallback: ProgressCallback
) :
    ResponseListener {
    private val lock = Any()
    private var totalCount = 0
    private var failedCount = 0
    private var finishCount = 0
    fun download(directory: String?, fileName: String?, url: String?, priority: Int) {
        totalCount++
        progressListener.download(directory, fileName, url, this, priority)
    }

    fun download(filePathToUrlMap: Map<String?, String?>, priority: Int) {
        download(null, filePathToUrlMap, priority)
    }

    fun download(directory: String?, fileNameToUrlMap: Map<String?, String?>, priority: Int) {
        for ((url, fileName) in fileNameToUrlMap) {
            totalCount++
            progressListener.download(directory, fileName, url, this, priority)
        }
    }

    private fun reportProgress() {
        synchronized(lock) {
            val percent =
                if (totalCount == 0) 100 else Math.floor((finishCount * 100f / totalCount).toDouble())
                    .toInt()
            progressCallback.onProgress(percent)
            if (failedCount + finishCount == totalCount) {
                progressCallback.onFinish(failedCount)
                Logger.d(
                    TAG,
                    "reportProgress() : onFinish() : $totalCount"
                )
                reset()
            }
        }
    }

    private fun reset() {
        totalCount = 0
        failedCount = 0
    }

    override fun onSuccess() {
        synchronized(lock) {
            finishCount++
            reportProgress()
            Logger.d(
                TAG,
                "onSuccess() : finishCount : $finishCount"
            )
        }
    }

    override fun onFailure() {
        synchronized(lock) {
            failedCount++
            reportProgress()
        }
    }

    interface ProgressCallback {
        fun onProgress(percent: Int)
        fun onFinish(failedCount: Int)
    }

    companion object {
        private val TAG = DownloadProgressManager::class.java.simpleName
    }
}