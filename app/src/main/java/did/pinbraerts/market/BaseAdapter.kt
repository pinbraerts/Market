package did.pinbraerts.market

import android.support.v7.widget.RecyclerView
import java.util.*

abstract class BaseAdapter(
    protected val recyclerView: RecyclerView,
    protected val data: ArrayList<MarketItem>
): RecyclerView.Adapter<BaseViewHolder>() {
    fun updateInitial() {
        (0 until data.size).forEach {
            this.afterUpdate(it)
        }
        this.anyUpdate()
    }

    override fun getItemCount()
        = data.size

    override fun onBindViewHolder(vh: BaseViewHolder, position: Int) {
        vh.setItem(position, data[position])
    }

    open fun insert(item: MarketItem, index: Int) {
        data.add(index, item)
        afterUpdate(index)
        notifyItemInserted(index)
        anyUpdate()
    }

    fun add(item: MarketItem) =
        insert(item, indexFor(item.color))

    fun indexFor(color: Int): Int {
        var i = data.binarySearch { color - it.color }
        if(i < 0) i = -(i + 1)
        return i
    }

    open fun remove(index: Int) {
        beforeUpdate(index)
        data.removeAt(index)
        notifyItemRemoved(index)
        anyUpdate()
    }

    open fun move(fromPosition: Int, toPosition: Int) {
        data.add(toPosition, data.removeAt(fromPosition))
//        when {
//            fromPosition == toPosition -> return
//            fromPosition > toPosition ->
//                for (i in toPosition until fromPosition)
//                    Collections.swap(MarketData.data, i, i + 1)
//            else ->
//                for (i in fromPosition until toPosition)
//                    Collections.swap(MarketData.data, i, i + 1)
//        }
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

    fun addAll(items: Collection<MarketItem>) {
        items.forEach {
            it.color = MarketData.colorPreferences.getOrElse(it.name, { it.color })
            add(it)
        }
    }

    open fun removeAll(fromPosition: Int, toPosition: Int) {
        (fromPosition until toPosition).forEach { beforeUpdate(it) }
        data.subList(fromPosition, toPosition).clear()
        notifyItemRangeRemoved(fromPosition, toPosition - fromPosition)
        anyUpdate()
    }

    fun removeAll(fromPosition: Int) =
        removeAll(fromPosition, data.size)

    fun removeAll() =
        removeAll(0)

    fun get(index: Int) =
        MockedItem(this, index)

    data class MockedItem(
        val adapter: BaseAdapter,
        val position: Int,
    ) {
        public var color: Int
            get() = adapter.data[position].color
            set(value) = adapter.setColor(position, value)

        public var name: String
            get() = adapter.data[position].name
            set(value) = adapter.setName(position, value)

        public var amount: String
            get() = adapter.data[position].amount
            set(value) = adapter.setAmount(position, value)

        public var price: Float
            get() = adapter.data[position].price
            set(value) = adapter.setPrice(position, value)

        public var cost: Float
            get() = adapter.data[position].cost
            set(value) = adapter.setCost(position, value)

        public val discrepancy: Float
            get() = adapter.data[position].discrepancy
    }

    open fun setColor(index: Int, color: Int, shouldUpdateView: Boolean = true) {
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

        anyUpdate()
    }

    open fun setName(index: Int, name: String, shouldUpdateView: Boolean = true) {
        beforeUpdate(index)

        data[index].name = name
        if(shouldUpdateView)
            getViewHolder(index)?.setName(name)

        afterUpdate(index)
        anyUpdate()
    }

    open fun setAmount(index: Int, amount: String, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].amount = amount
        if(shouldUpdateView)
            getViewHolder(index)?.setAmount(amount)

        afterUpdate(index)
        anyUpdate()
    }

    open fun setWeight(index: Int, weight: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].weight = weight
        if(shouldUpdateView)
            getViewHolder(index)?.setWeight(weight)

        afterUpdate(index)
        anyUpdate()
    }

    open fun setPrice(index: Int, price: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].price = price
        if(shouldUpdateView)
            getViewHolder(index)?.setPrice(price)

        afterUpdate(index)
        anyUpdate()
    }

    open fun setCost(index: Int, cost: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        data[index].cost = cost
        if(shouldUpdateView)
            getViewHolder(index)?.setCost(cost)

        afterUpdate(index)
        anyUpdate()
    }

    fun getViewHolder(index: Int) =
        recyclerView.findViewHolderForAdapterPosition(index) as? BaseViewHolder

    open fun beforeUpdate(index: Int) { }

    open fun afterUpdate(index: Int) { }

    open fun anyUpdate() { }

    fun paste(s: String) {
        addAll(s.split(',', '\n')
            .filter(String::isNotBlank)
            .map(MarketItem::fromClipboard))
    }

    fun toPlainText() =
        data.filter(MarketItem::isValid).joinToString("\n", transform = MarketItem::toClipboard)
}
