package did.pinbraerts.market

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class VerifyAdapter(
    recyclerView: RecyclerView,
    data: ArrayList<MarketItem>,
    private val summaryCost: TextView,
    private val summaryDiscrepancy: TextView
): BaseAdapter(recyclerView, data) {
    private var sumCost: Float = 0f
    private var sumDisc: Float = 0f

    init {
        updateInitial()
    }

    class ViewHolder(
        view: View,
        private val adapter: VerifyAdapter,
        val v_color: View = view.findViewById(R.id.v_color),
        val tv_name: TextView = view.findViewById(R.id.tv_name),
        val tv_price: TextView = view.findViewById(R.id.tv_price),
        val et_weight: EditText = view.findViewById(R.id.et_weight),
        val tv_cost: TextView = view.findViewById(R.id.tv_cost),
        val tv_delta: TextView = view.findViewById(R.id.tv_delta),
    ): BaseViewHolder(view) {
        override fun setColor(color: Int) {
            v_color.setBackgroundColor(MarketData.palette[color])
        }

        override fun setName(name: String) {
            tv_name.text = name
        }

        override fun setAmount(amount: String) { }

        override fun setPrice(price: Float) {
            tv_price.text = NumberFormat.getInstance().format(price)
        }

        override fun setCost(cost: Float) {
            tv_cost.text = NumberFormat.getInstance().format(cost)
        }

        override fun setWeight(weight: Float) {
            with(et_weight) {
                removeTextChangedListener(listener)
                et_weight.setText(NumberFormat.getInstance().format(weight), TextView.BufferType.EDITABLE)
                addTextChangedListener(listener)
            }
        }

        fun setDiscrepancy(discrepancy: Float) {
            tv_delta.text = NumberFormat.getInstance().format(discrepancy)
            if(discrepancy > -0.1)
                tv_delta.setTextColor(ContextCompat.getColor(tv_delta.context, R.color.correct))
            else
                tv_delta.setTextColor(ContextCompat.getColor(tv_delta.context, R.color.wrong))
        }

        override fun setItem(position: Int, item: MarketItem) {
            super.setItem(position, item)
            setDiscrepancy(item.discrepancy)
        }

        private val listener = TextWatchers.PlainNumber { newData ->
            adapter.setWeight(adapterPosition, newData.toFloat(), false)
        }

        init {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                et_weight.showSoftInputOnFocus = true
            }
            tv_name.setHorizontallyScrolling(false)
            tv_name.maxLines = 3

            et_weight.addTextChangedListener(listener)
        }
    }

    override fun beforeUpdate(index: Int) {
        sumCost -= data[index].cost
        sumDisc -= data[index].discrepancy
    }

    override fun afterUpdate(index: Int) {
        sumCost += data[index].cost
        sumDisc += data[index].discrepancy

        (getViewHolder(index) as? ViewHolder)?.setDiscrepancy(data[index].discrepancy)
    }

    override fun anyUpdate() {
        summaryCost.text = NumberFormat.getInstance().format(sumCost)
        summaryDiscrepancy.text = NumberFormat.getInstance().format(sumDisc)
        if(sumDisc > -0.1)
            summaryDiscrepancy.setTextColor(ContextCompat.getColor(recyclerView.context, R.color.correct))
        else
            summaryDiscrepancy.setTextColor(ContextCompat.getColor(recyclerView.context, R.color.wrong))
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_verify, p0, false), this)
}