package did.pinbraerts.market

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MarketItemAdapter(
    val activity: MainActivity,
    val data: MarketItems = arrayListOf()
): RecyclerView.Adapter<MarketItemViewHolder>(), ColorPicker.OnColorPickedListener {
    private var focusedHolder: MarketItemViewHolder? = null
    private val sections: IntArray = IntArray(8)
    private val colorOrder: IntArray = IntArray(8) { 7 - it }

    fun post() {
        activity.rv_items.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val paint: Paint = Paint().apply {
                style = Paint.Style.STROKE
                color = ContextCompat.getColor(activity, android.R.color.darker_gray)
            }

            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val left = parent.paddingLeft.toFloat()
                val right = parent.width.toFloat() - parent.paddingRight

                sections.forEach {
                    parent.findViewHolderForAdapterPosition(it)?.let { vh ->
                        paint.color = MarketData.palette[data[it].color]
                        val y = vh.itemView.y - 10
                        c.drawLine(left, y, right, y, paint)
                    }
                }
            }
        })
    }

    val focusChangedListener = View.OnFocusChangeListener { view, focused ->
        focusedHolder =
            if(focused) {
//                (view as? EditText)?.showKeyboard()
                activity.rv_items.findContainingViewHolder(view) as? MarketItemViewHolder
            }
            else null
    }

    override fun onColorPicked(color: Int) {
        focusedHolder?.let {
            setColor(it.adapterPosition, color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketItemViewHolder =
        MarketItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.market_item, parent, false
            ), this
        )

    override fun onBindViewHolder(holder: MarketItemViewHolder, position: Int) {
        holder.setItem(activity.state, data[position])
    }

    override fun getItemCount(): Int =
        data.size

    private fun getViewHolder(index: Int) =
        activity.rv_items.findViewHolderForAdapterPosition(index) as? MarketItemViewHolder

    private fun insert(item: MarketItem, index: Int) {
        data.add(index, item)

        afterUpdate(index)
        notifyItemInserted(index)
    }

    fun add(item: MarketItem) {
        MarketData.preferenceOrNull(item)?.let {
            item.color = it
        }
        val section = order(item.color)
        (section + 1 until sections.size).forEach {
            ++sections[it]
        }
        return insert(item, sections[section])
    }

    private fun order(color: Int) = colorOrder[color]

    private fun sectionIndex(itemIndex: Int): Int {
        var i = sections.binarySearch(itemIndex)
        if(i < 0) i = -(i + 1)
        while(i > 0 && sections[i - 1] == itemIndex)
            --i
        return i
    }

    fun remove(index: Int) {
        beforeUpdate(index)
        val section = sectionIndex(index)
        (section + 1 until sections.size).forEach {
            --sections[it]
        }
        data.removeAt(index)
        notifyItemRemoved(index)
    }

    fun move(fromPosition: Int, toPosition: Int) {
        val fromSection = order(data[fromPosition].color)
        val toSection = sectionIndex(toPosition)

        if(fromSection < toSection) {
            (fromSection + 1 .. toSection).forEach {
                --sections[it]
            }
        }
        else if(fromSection > toSection) {
            (toSection + 1 .. fromSection).forEach {
                ++sections[it]
            }
        }

        data.add(toPosition, data.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun moveToSection(fromPosition: Int, section: Int): Int {
        val fromSection = order(data[fromPosition].color)
        val toPosition = sections[section]

        if(fromSection < section) {
            (fromSection + 1 .. section).forEach {
                --sections[it]
            }
        }
        else if(fromSection > section) {
            (section + 1 .. fromSection).forEach {
                ++sections[it]
            }
        }

        data.add(toPosition, data.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
        return toPosition
    }

    fun clear() {
        sections.fill(0)
        data.clear()
        notifyItemRangeRemoved(0, data.size)
    }

    fun setColor(index: Int, color: Int, shouldUpdateView: Boolean = true) {
        if(color == data[index].color)
            return

        val i = moveToSection(index, order(color))

        data[i].color = color
        if(shouldUpdateView)
            getViewHolder(i)?.setColor(color)
    }

    fun setName(index: Int, name: String, shouldUpdateView: Boolean = true) {
        data[index].name = name
        if(shouldUpdateView)
            getViewHolder(index)?.setName(name)

        MarketData.preferenceOrNull(data[index])?.let {
            setColor(index, it)
        }
    }

    fun setAmount(index: Int, amount: String, shouldUpdateView: Boolean = false) {
        data[index].amount = amount
        if(shouldUpdateView)
            getViewHolder(index)?.setAmount(amount)
    }

    fun setWeight(index: Int, weight: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].weight = weight
        getViewHolder(index)?.apply{
            if(shouldUpdateView)
                setWeight(weight)
            setDiscrepancy(data[index].discrepancy)
        }
        afterUpdate(index)
    }

    fun setPrice(index: Int, price: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].price = price
        getViewHolder(index)?.apply {
            if(shouldUpdateView)
                setPrice(price)
            setDiscrepancy(data[index].discrepancy)
        }

        afterUpdate(index)
    }

    fun setCost(index: Int, cost: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].cost = cost
        getViewHolder(index)?.apply {
            if(shouldUpdateView)
                setCost(cost)
            setDiscrepancy(data[index].discrepancy)
        }

        afterUpdate(index)
    }

    fun setState(newState: MainActivity.State) {
        activity.rv_items.visibleViewHolders().forEach {
            if(it is MarketItemViewHolder)
                it.setState(newState, data[it.adapterPosition])
        }
    }

    private fun beforeUpdate(index: Int) {
        with(data[index]) {
            activity.summary.cost -= cost
            activity.summary.discrepancy -= discrepancy
        }
    }

    private fun afterUpdate(index: Int) {
        with(data[index]) {
            activity.summary.cost += cost
            activity.summary.discrepancy += discrepancy
        }
    }

    fun paste(s: String) {
        s.split(',', '\n')
            .filter(String::isNotBlank)
            .forEach {
                add(MarketItem.from(it))
            }
    }

    fun copy() = data.joinToString("\n", transform = MarketItem::toString)

    fun load() {
        if(data.isNotEmpty())
            return
        if(!activity.getFileStreamPath(activity.filename).exists())
            return
        ObjectInputStream(activity.openFileInput(activity.filename)).use { stream ->
            when(val v = stream.readObject()) {
                is ArrayList<*> -> v.filterIsInstance<MarketItem>().forEach(::add)
                else -> return
            }
        }
    }

    fun save() =
        ObjectOutputStream(activity.openFileOutput(activity.filename, Context.MODE_PRIVATE)).use {
            it.writeObject(data)
        }
}