package did.pinbraerts.market

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.NumberFormat
import java.text.ParseException

object TextWatchers {
    class PlainString(private val target: (String) -> Unit): TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int) {
            // do nothing
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int) {
            // do nothing
        }

        override fun afterTextChanged(e: Editable) {
            target(e.toString())
        }
    }

    class PlainNumber(private val target: (Number) -> Unit): TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int) {
            // do nothing
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int) {
            // do nothing
        }

        override fun afterTextChanged(e: Editable) {
            target(MarketData.parse(e.toString()))
        }
    }
}
