package did.pinbraerts.market

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView


fun String.toFloatOrZero() =
    toFloatOrNull() ?: 0f

fun String.toIntOrZero() =
    toIntOrNull() ?: 0

fun View.hide() {
    if(visibility == View.VISIBLE)
        visibility = View.GONE
}

fun View.show() {
    if(visibility != View.VISIBLE)
        visibility = View.VISIBLE
}

fun RecyclerView.visibleViewHolders() =
    (0 until childCount).map(::getChildAt).map(::getChildViewHolder)

fun EditText.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
//    imm.toggleSoftInput(0, 0)
}

fun EditText.freeze() {
    isEnabled = false
    isFocusable = false
    isFocusableInTouchMode = false
    isClickable = false
}

fun EditText.unFreeze() {
    isEnabled = true
    isFocusable = true
    isFocusableInTouchMode = true
    isClickable = true
}

//fun EditText.hideKeyboard() {
//    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.hideSoftInputFromWindow(this.windowToken, 0)
//}
