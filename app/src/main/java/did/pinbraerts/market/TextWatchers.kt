package did.pinbraerts.market

import android.text.Editable
import android.text.TextWatcher

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
            target(e.toString().toFloatOrZero())
        }
    }
}
