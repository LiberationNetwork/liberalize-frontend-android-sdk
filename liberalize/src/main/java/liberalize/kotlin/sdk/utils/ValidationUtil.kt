package liberalize.kotlin.sdk.utils

import android.text.TextUtils
import java.util.*
import java.util.regex.Pattern

internal object ValidationUtil {
    private const val MAXIMUM_VALID_YEAR_DIFFERENCE = 20

    enum class CardType(val regex: String) {
        VISA("^4[0-9]{12}(?:[0-9]{3})?\$"),
        VISA_ELECTRON("^(4026|417500|4508|4844|491(3|7))"),
        MASTERCARD("^5[1-5][0-9]{14}\$"),
        MAESTRO("^(5018|5020|5038|6304|6759|676[1-3])"),
        AMERICAN_EXPRESS("^3[47][0-9]{13}\$"),
        DINERS_CLUB("^3(?:0[0-5]|[68][0-9])[0-9]{11}\$"),
        JCB("^6(?:011|5[0-9]{2})[0-9]{12}\$"),
        DISCOVERY("^(?:2131|1800|35\\\\d{3})\\\\d{11}\$"),
    }

    fun isCardNumberValid(number: String?): Boolean {
        if (number.isNullOrEmpty()) return false
        return CardType.values().any {
            checkRegex(it.regex, number)
        }
    }

    private fun checkRegex(regex: String, value: String): Boolean {
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(value)
        return matcher.matches()
    }

    fun isExpiryDateValid(monthString: String, yearString: String): Boolean {
        if (TextUtils.isEmpty(monthString)) {
            return false
        }
        if (TextUtils.isEmpty(yearString)) {
            return false
        }
        if (!TextUtils.isDigitsOnly(monthString) || !TextUtils.isDigitsOnly(yearString)) {
            return false
        }
        val month = monthString.toInt()
        if (month < 1 || month > 12) {
            return false
        }
        val currentYear: Int = getCurrentTwoDigitYear()
        val year: Int
        val yearLength = yearString.length
        year = if (yearLength == 2) {
            yearString.toInt()
        } else {
            return false
        }
        if (year == currentYear && month < getCurrentMonth()) {
            return false
        }
        if (year < currentYear) {
            // account for century-overlapping in 2-digit year representations
            val adjustedYear = year + 100
            if (adjustedYear - currentYear > MAXIMUM_VALID_YEAR_DIFFERENCE) {
                return false
            }
        }
        return year <= currentYear + MAXIMUM_VALID_YEAR_DIFFERENCE
    }

    private fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }

    private fun getCurrentTwoDigitYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR) % 100
    }
}