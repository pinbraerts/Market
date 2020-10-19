package did.pinbraerts.market

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MarketItemViewHolder(
    view: View,
    state: MainActivity.State,
    val v_color: View = view.findViewById(R.id.v_color),
    val et_name: EditText = view.findViewById(R.id.et_name),
    val et_amount: EditText = view.findViewById(R.id.et_amount),
    val tv_asterisk: TextView = view.findViewById(R.id.tv_asterisk),
    val et_cost: EditText = view.findViewById(R.id.et_cost),
    val tv_equals: TextView = view.findViewById(R.id.tv_equals),
    val et_price: EditText = view.findViewById(R.id.et_price),
    val v_divider: View = view.findViewById(R.id.v_divider),
    val tv_discrepancy: TextView = view.findViewById(R.id.tv_discrepancy)
): RecyclerView.ViewHolder(view) {
    init {
        setState(state)
    }

    fun setState(state: MainActivity.State) {
        when(state) {
            MainActivity.State.PLAN -> {
                et_name.unFreeze()
                et_amount.unFreeze()
                et_amount.inputType = InputType.TYPE_CLASS_TEXT
                et_amount.text.clear()
                tv_asterisk.hide()
                et_cost.hide()
                tv_equals.hide()
                et_price.hide()
                tv_discrepancy.hide()
            }
            MainActivity.State.BUY -> {
                et_name.freeze()
                et_amount.freeze()

                tv_asterisk.show()
                et_cost.unFreeze()
                et_cost.show()

                tv_equals.show()
                et_price.unFreeze()
                et_price.show()

                tv_discrepancy.hide()
            }
            MainActivity.State.VERIFY -> {
                et_name.freeze()
                et_amount.unFreeze()
                et_amount.inputType = InputType.TYPE_CLASS_NUMBER
                et_amount.text.clear()

                tv_asterisk.show()
                et_cost.show()
                et_cost.freeze()

                tv_equals.show()
                et_price.show()
                et_price.freeze()

                tv_discrepancy.show()
            }
        }
    }
}
