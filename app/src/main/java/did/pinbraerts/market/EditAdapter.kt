package did.pinbraerts.market

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import java.text.NumberFormat

class EditAdapter(
    recyclerView: RecyclerView,
    data: ArrayList<MarketItem>,
    private val colorPicker: ColorPicker,
    private val summaryCost: TextView
): BaseAdapter(recyclerView, data) {
    private var sumCost: Float = 0f
    private var focusedChild: ViewHolder? = null
    private val focusChangedListener = View.OnFocusChangeListener { view, focused ->
        focusedChild = if(focused) recyclerView.findContainingViewHolder(view) as ViewHolder?
            else null
    }

    init {
        updateInitial()

        colorPicker.setOnColorPickerListener {
            focusedChild?.apply {
                setColor(adapterPosition, it)
            }
        }
    }

    class ViewHolder(
        view: View,
        private val adapter: EditAdapter,
        public val v_color: View = view.findViewById(R.id.v_color),
        public val et_name: EditText = view.findViewById(R.id.et_name),
        public val et_amount: EditText = view.findViewById(R.id.et_amount),
        public val et_price: EditText = view.findViewById(R.id.et_price),
        public val et_cost: EditText = view.findViewById(R.id.et_cost),
    ): BaseViewHolder(view) {
        private val listeners = arrayOf(
            TextWatchers.PlainString { newData ->
                adapter.setName(adapterPosition, newData, false)
            },
            TextWatchers.PlainString { newData ->
                adapter.setAmount(adapterPosition, newData, false)
            },
            TextWatchers.PlainNumber { newData ->
                adapter.setPrice(adapterPosition, newData.toFloat(), false)
            },
            TextWatchers.PlainNumber { newData ->
                adapter.setCost(adapterPosition, newData.toFloat(), false)
            }
        )

        init {
            et_name.addTextChangedListener(listeners[0])
            et_amount.addTextChangedListener(listeners[1])
            et_price.addTextChangedListener(listeners[2])
            et_cost.addTextChangedListener(listeners[3])

            et_name.onFocusChangeListener = adapter.focusChangedListener
            et_amount.onFocusChangeListener = adapter.focusChangedListener
            et_price.onFocusChangeListener = adapter.focusChangedListener
            et_cost.onFocusChangeListener = adapter.focusChangedListener

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                et_name.showSoftInputOnFocus = true
                et_amount.showSoftInputOnFocus = true
                et_price.showSoftInputOnFocus = true
                et_cost.showSoftInputOnFocus = true
            }
            et_name.setHorizontallyScrolling(false)
            et_name.maxLines = 3
        }

        override fun setColor(color: Int) {
            v_color.setBackgroundColor(MarketData.palette[color])
        }

        override fun setName(name: String) {
            with(et_name) {
                removeTextChangedListener(listeners[0])
                setText(name, TextView.BufferType.EDITABLE)
                addTextChangedListener(listeners[0])
            }
        }

        override fun setAmount(amount: String) {
            with(et_amount) {
                removeTextChangedListener(listeners[1])
                setText(amount, TextView.BufferType.EDITABLE)
                addTextChangedListener(listeners[1])
            }
        }

        override fun setPrice(price: Float) {
            with(et_price) {
                removeTextChangedListener(listeners[2])
                setText(NumberFormat.getInstance().format(price), TextView.BufferType.EDITABLE)
                addTextChangedListener(listeners[2])
            }
        }

        override fun setCost(cost: Float) {
            with(et_cost) {
                removeTextChangedListener(listeners[3])
                setText(NumberFormat.getInstance().format(cost), TextView.BufferType.EDITABLE)
                addTextChangedListener(listeners[3])
            }
        }

        override fun setItem(position: Int, item: MarketItem) {
            super.setItem(position, item)
            et_name.requestFocus()
            et_name.showKeyboard()
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_edit, p0, false), this)

    override fun beforeUpdate(index: Int) {
        sumCost -= data[index].cost
    }

    override fun afterUpdate(index: Int) {
        sumCost += data[index].cost
    }

    override fun anyUpdate() {
        summaryCost.text = NumberFormat.getInstance().format(sumCost)
    }
}
