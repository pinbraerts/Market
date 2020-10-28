package did.pinbraerts.market

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MarketItemAdapter(
    val filename: String,
    val data: MarketItems,
    val activity: MainActivity
): RecyclerView.Adapter<MarketItemViewHolder>() {
    private var focusedHolder: MarketItemViewHolder? = null

    val focusChangedListener = View.OnFocusChangeListener { view, focused ->
        focusedHolder =
            if(focused) {
//                (view as? EditText)?.showKeyboard()
                activity.rv_items.findContainingViewHolder(view) as? MarketItemViewHolder
            }
            else null
    }

    fun colorChanged(color: Int) {
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

    fun getViewHolder(index: Int) =
        activity.rv_items.findViewHolderForAdapterPosition(index) as? MarketItemViewHolder

    fun insert(item: MarketItem, index: Int) {
        data.add(index, item)
        afterUpdate(index)
        notifyItemInserted(index)
    }

    fun add(item: MarketItem) {
        item.color = MarketData.preference(item)
        return insert(item, indexFor(item.color))
    }

    private fun indexFor(color: Int): Int {
        var i = data.binarySearch { color - it.color }
        if(i < 0) i = -(i + 1)
        return i
    }

    fun remove(index: Int) {
        beforeUpdate(index)
        data.removeAt(index)
        notifyItemRemoved(index)
    }

    fun move(fromPosition: Int, toPosition: Int) {
        data.add(toPosition, data.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
    }

//    open fun insertAll(items: Collection<MarketItem>, index: Int) {
//        val new_items = items.map {
//            it.color = MarketData.colorPreferences.getOrElse(it.name, { it.color })
//            it
//        }.toList()
//        data.addAll(index, new_items)
//        (index until index + new_items.size).forEach { afterUpdate(it) }
//        notifyItemRangeInserted(index, items.size)
//        anyUpdate()
//    }

    fun removeAll(fromPosition: Int, toPosition: Int) {
        (fromPosition until toPosition).forEach { beforeUpdate(it) }
        data.subList(fromPosition, toPosition).clear()
        notifyItemRangeRemoved(fromPosition, toPosition - fromPosition)
    }

    fun removeAll(fromPosition: Int) =
        removeAll(fromPosition, data.size)

    fun removeAll() =
        removeAll(0)

    fun setColor(index: Int, color: Int, shouldUpdateView: Boolean = true) {
        if(color == data[index].color)
            return

        var i = indexFor(color)
        if(index < i)
            i -= 1

        beforeUpdate(index)
        data[index].color = color
        if(shouldUpdateView)
            getViewHolder(index)?.setColor(color)
        afterUpdate(index)

        move(index, i)
    }

    fun setName(index: Int, name: String, shouldUpdateView: Boolean = true) {
        beforeUpdate(index)

        data[index].name = name
        if(shouldUpdateView)
            getViewHolder(index)?.setName(name)

        setColor(index, MarketData.preference(data[index]))

        afterUpdate(index)
    }

    fun setAmount(index: Int, amount: String, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].amount = amount
        if(shouldUpdateView)
            getViewHolder(index)?.setAmount(amount)

        afterUpdate(index)
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
        if(!activity.getFileStreamPath(filename).exists())
            return
        ObjectInputStream(activity.openFileInput(filename)).use { stream ->
            when(val v = stream.readObject()) {
                is ArrayList<*> -> v.filterIsInstance<MarketItem>().forEach(::add)
                else -> return
            }
        }
    }

    fun save() =
        ObjectOutputStream(activity.openFileOutput(filename, Context.MODE_PRIVATE)).use {
            it.writeObject(data)
        }
}