package liberalize.kotlin.sdk.card

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.util.AttributeSet
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.R
import com.google.android.material.textfield.TextInputEditText


internal class ExpiryDateEdittext @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    style: Int = R.attr.editTextStyle
) : TextInputEditText(context, attributes, style) {
    companion object {
        private const val MAX_LENGTH = 5
    }

    private var isValid = false

    private var stoppedFormat = false

    init {
        listenForTextChange()
    }

    private fun listenForTextChange() {
        doAfterTextChanged {
            if (stoppedFormat) return@doAfterTextChanged
            stoppedFormat = true
            it?.let {
                formatExpiryDate(it)
                stoppedFormat = false
            }
        }
    }

    /**
     * add span
     */
    private fun formatExpiryDate(expiryDate: Editable) {
        var textLength = expiryDate.length
        val spans = expiryDate.getSpans(0, expiryDate.length, SlashSpan::class.java)
        for (i in spans.indices) {
            expiryDate.removeSpan(spans[i])
        }
        if (MAX_LENGTH > 0 && textLength > MAX_LENGTH - 1) {
            expiryDate.replace(MAX_LENGTH, textLength, "")
            --textLength
        }
        for (i in 1..(textLength - 1) / 2) {
            val end = i * 2 + 1
            val start = end - 1
            val marginSPan = SlashSpan()
            expiryDate.setSpan(marginSPan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}