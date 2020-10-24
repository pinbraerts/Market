package did.pinbraerts.market

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat

class MarketItemViewHolder(
    view: View,
    state: MainActivity.State,
    val adapter: MarketItemAdapter,
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
    private val listeners = arrayOf(
        TextWatchers.PlainString { newData ->
            adapter.setName(adapterPosition, newData, false)
        },
        TextWatchers.PlainString { newData ->
            adapter.setAmount(adapterPosition, newData, false)
        },
        TextWatchers.PlainNumber { newData ->
            adapter.setWeight(adapterPosition, newData.toFloat(), false)
        },
        TextWatchers.PlainNumber { newData ->
            adapter.setPrice(adapterPosition, newData.toFloat(), false)
        },
        TextWatchers.PlainNumber { newData ->
            adapter.setCost(adapterPosition, newData.toFloat(), false)
        }
    )

    init {
        setState(state)

        et_name.onFocusChangeListener = adapter.focusChangedListener
        et_amount.onFocusChangeListener = adapter.focusChangedListener
        et_cost.onFocusChangeListener = adapter.focusChangedListener
        et_price.onFocusChangeListener = adapter.focusChangedListener

        et_name.addTextChangedListener(listeners[0])
        et_amount.addTextChangedListener(listeners[1])
        et_cost.addTextChangedListener(listeners[3])
        et_price.addTextChangedListener(listeners[4])
    }

    fun amountUpdateState(state: MainActivity.State) {
        with(et_amount) {
            when (state) {
                MainActivity.State.PLAN -> {
                    if(inputType != InputType.TYPE_CLASS_TEXT) {
                        et_amount.removeTextChangedListener(listeners[2])
                        et_amount.inputType = InputType.TYPE_CLASS_TEXT
                        et_amount.text.clear()
                        et_amount.addTextChangedListener(listeners[1])
                    }
                    et_amount.unFreeze()
                }
                MainActivity.State.BUY -> {
                    if(inputType != InputType.TYPE_CLASS_TEXT) {
                        et_amount.removeTextChangedListener(listeners[2])
                        et_amount.inputType = InputType.TYPE_CLASS_TEXT
                        et_amount.text.clear()
                        et_amount.addTextChangedListener(listeners[1])
                    }
                    et_amount.freeze()
                }
                MainActivity.State.VERIFY -> {
                    if(et_amount.inputType != InputType.TYPE_CLASS_NUMBER) {
                        et_amount.removeTextChangedListener(listeners[1])
                        et_amount.inputType = InputType.TYPE_CLASS_NUMBER
                        et_amount.text.clear()
                        et_amount.addTextChangedListener(listeners[2])
                    }
                }
            }
        }
    }

    fun setState(state: MainActivity.State) {
        amountUpdateState(state)
        when(state) {
            MainActivity.State.PLAN -> {
                et_name.unFreeze()

                tv_asterisk.hide()
                et_cost.hide()
                tv_equals.hide()
                et_price.hide()
                tv_discrepancy.hide()
            }
            MainActivity.State.BUY -> {
                et_name.freeze()

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

    fun setColor(color: Int) {
        v_color.setBackgroundColor(MarketData.palette[color])
    }

    fun setName(name: String) {
        with(et_name) {
            removeTextChangedListener(listeners[0])
            setText(name, TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[0])
        }
    }

    fun setAmount(amount: String) {
        with(et_amount) {
            removeTextChangedListener(listeners[1])
            setText(amount, TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[1])
        }
    }

    fun setWeight(weight: Float) {
        with(et_amount) {
            removeTextChangedListener(listeners[2])
            setText(NumberFormat.getInstance().format(weight), TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[2])
        }
    }

    fun setPrice(price: Float) {
        with(et_price) {
            removeTextChangedListener(listeners[3])
            setText(NumberFormat.getInstance().format(price), TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[3])
        }
    }

    fun setCost(cost: Float) {
        with(et_cost) {
            removeTextChangedListener(listeners[4])
            setText(NumberFormat.getInstance().format(cost), TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[4])
        }
    }

    fun setItem(state: MainActivity.State, item: MarketItem) {
        setState(state)
        setColor(item.color)
        setName(item.name)
        if(state == MainActivity.State.VERIFY) setWeight(item.weight)
        else setAmount(item.amount)
        setPrice(item.price)
        setCost(item.cost)
        et_name.requestFocus()
        et_name.showKeyboard()
    }
}
