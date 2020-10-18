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
            try {
                val b = NumberFormat.getInstance().parse(e.toString()) ?: 0
                target(b)
            } catch (e: ParseException) {
                target(0)
            }
        }
    }
}

fun String.toFloatOrZero() =
    toFloatOrNull() ?: 0f

fun String.toIntOrZero() =
    toIntOrNull() ?: 0

fun EditText.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
//    imm.toggleSoftInput(0, 0)
}

//fun EditText.hideKeyboard() {
//    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.hideSoftInputFromWindow(this.windowToken, 0)
//}
