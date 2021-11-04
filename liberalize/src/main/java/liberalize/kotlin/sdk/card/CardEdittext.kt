package liberalize.kotlin.sdk.card

import android.content.Context
import android.text.Editable
import android.text.Spanned
import android.util.AttributeSet
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.R
import com.google.android.material.textfield.TextInputEditText

internal class CardEdittext @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    style: Int = R.attr.editTextStyle
) : TextInputEditText(context, attributes, style) {

    companion object {
        private val DEFAULT_SPACE_INDICES = listOf(4, 8, 12)
    }

    init {
        listenForTextChange()
    }

    private fun listenForTextChange() {
        doAfterTextChanged {
            it?.let {
                val paddingSpans = it.getSpans(0, it.length, SpaceSpan::class.java)
                for (span in paddingSpans) {
                    it.removeSpan(span)
                }
                addSpans(it)
            }
        }
    }

    /**
     * Adding span
     */
    private fun addSpans(editable: Editable) {
        val length = editable.length
        for (index in DEFAULT_SPACE_INDICES) {
            if (index <= length) {
                editable.setSpan(
                    SpaceSpan(), index - 1, index,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}