package liberalize.kotlin.sample.utils

import android.content.Context
import android.graphics.Color
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RelativeLayout.CENTER_IN_PARENT
import androidx.appcompat.app.AlertDialog

object DialogUtil {
    @JvmStatic
    fun createLoadingProgress(context: Context): AlertDialog {
        val relativeLayout = RelativeLayout(context)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        relativeLayout.setBackgroundColor(Color.TRANSPARENT)
        relativeLayout.layoutParams = params
        params.addRule(CENTER_IN_PARENT)
        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 20, 0, 20)
        progressBar.layoutParams = params
        relativeLayout.addView(progressBar)
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(relativeLayout)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.window?.let {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(it.attributes)
                layoutParams.width = 300
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                it.attributes = layoutParams
            }
        }
        return dialog
    }
}