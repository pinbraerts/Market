package did.pinbraerts.market

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class MarketItemViewHolder(
    view: View,
    private val adapter: MarketItemAdapter,
    private val v_color: View = view.findViewById(R.id.v_color),
    private val et_name: EditText = view.findViewById(R.id.et_name),
    private val et_amount: EditText = view.findViewById(R.id.et_amount),
    private val tv_asterisk: TextView = view.findViewById(R.id.tv_asterisk),
    private val et_cost: EditText = view.findViewById(R.id.et_cost),
    private val tv_equals: TextView = view.findViewById(R.id.tv_equals),
    private val et_price: EditText = view.findViewById(R.id.et_price),
    private val tv_discrepancy: TextView = view.findViewById(R.id.tv_discrepancy)
): RecyclerView.ViewHolder(view) {
    companion object {
        const val TYPE_NUMBER = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        const val TYPE_TEXT = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    private val listeners = arrayOf(
        TextWatchers.PlainString { newData ->
            adapter.setName(bindingAdapterPosition, newData, false)
        },
        TextWatchers.PlainString { newData ->
            adapter.setAmount(bindingAdapterPosition, newData, false)
        },
        TextWatchers.PlainNumber { newData ->
            adapter.setWeight(bindingAdapterPosition, newData.toFloat(), false)
        },
        TextWatchers.PlainNumber { newData ->
            adapter.setPrice(bindingAdapterPosition, newData.toFloat(), false)
        },
        TextWatchers.PlainNumber { newData ->
            adapter.setCost(bindingAdapterPosition, newData.toFloat(), false)
        }
    )

    private val format = DecimalFormat()

    init {
        et_name.onFocusChangeListener = adapter.focusChangedListener
        et_amount.onFocusChangeListener = adapter.focusChangedListener
        et_cost.onFocusChangeListener = adapter.focusChangedListener
        et_price.onFocusChangeListener = adapter.focusChangedListener

        et_name.addTextChangedListener(listeners[0])
        et_amount.addTextChangedListener(listeners[1])
        et_price.addTextChangedListener(listeners[3])
        et_cost.addTextChangedListener(listeners[4])
    }

    private fun amountUpdateState(state: MainActivity.State, item: MarketItem) {
        with(et_amount) {
            when (state) {
                MainActivity.State.PLAN -> {
                    if(inputType != TYPE_TEXT) {
                        et_amount.removeTextChangedListener(listeners[2])
                        et_amount.inputType = TYPE_TEXT
                        et_amount.setText(item.amount, TextView.BufferType.EDITABLE)
                        et_amount.addTextChangedListener(listeners[1])
                    }
                    et_amount.unFreeze()
                }
                MainActivity.State.BUY -> {
                    if(inputType != TYPE_TEXT) {
                        et_amount.removeTextChangedListener(listeners[2])
                        et_amount.inputType = TYPE_TEXT
                        et_amount.setText(item.amount, TextView.BufferType.EDITABLE)
                        et_amount.addTextChangedListener(listeners[1])
                    }
                    et_amount.freeze()
                }
                MainActivity.State.VERIFY -> {
                    if(et_amount.inputType != TYPE_NUMBER) {
                        et_amount.removeTextChangedListener(listeners[1])
                        et_amount.inputType = TYPE_NUMBER
                        et_amount.setText(format.format(item.weight), TextView.BufferType.EDITABLE)
                        et_amount.addTextChangedListener(listeners[2])
                    }
                    et_amount.unFreeze()
                }
            }
        }
    }

    fun setState(state: MainActivity.State, item: MarketItem) {
        amountUpdateState(state, item)
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
        v_color.setBackgroundColor(adapter.palette[color])
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
            setText(format.format(weight), TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[2])
        }
    }

    fun setPrice(price: Float) {
        with(et_price) {
            removeTextChangedListener(listeners[3])
            setText(format.format(price), TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[3])
        }
    }

    fun setCost(cost: Float) {
        with(et_cost) {
            removeTextChangedListener(listeners[4])
            setText(format.format(cost), TextView.BufferType.EDITABLE)
            addTextChangedListener(listeners[4])
        }
    }

    fun setDiscrepancy(discrepancy: Float) {
        with(tv_discrepancy) {
            text = format.format(discrepancy)
            setTextColor(ContextCompat.getColor(context,
                if(discrepancy > -0.1f) R.color.correct
                else R.color.wrong
            ))
        }
    }

    fun setItem(state: MainActivity.State, item: MarketItem) {
        setState(state, item)
        setColor(item.color)
        setName(item.name)
        if(state == MainActivity.State.VERIFY) setWeight(item.weight)
        else setAmount(item.amount)
        setPrice(item.price)
        setCost(item.cost)
        setDiscrepancy(item.discrepancy)
    }
}
