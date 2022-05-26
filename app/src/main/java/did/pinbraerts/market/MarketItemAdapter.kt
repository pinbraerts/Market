package did.pinbraerts.market

import android.graphics.Canvas
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class MarketItemAdapter(
    val activity: MainActivity,
    val array: PartialSortedArray<MarketItem> = PartialSortedArray(
        8,
        fun (item: MarketItem) = 7 - item.color
    )
): RecyclerView.Adapter<MarketItemViewHolder>(),
    ColorPicker.OnColorPickedListener {
    private var focusedHolder: MarketItemViewHolder? = null
    private val dbHelper = DBHelper(activity)
    var palette: IntArray = IntArray(0)
    private val colorPreferences: HashMap<String, Int> = HashMap()
    private val scope = CoroutineScope(Job())

    fun post() {
        activity.rv_items.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val paint: Paint = Paint().apply {
                style = Paint.Style.STROKE
                color = ContextCompat.getColor(activity, android.R.color.darker_gray)
            }

            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val left = parent.paddingLeft.toFloat()
                val right = parent.width.toFloat() - parent.paddingRight

                array.sections.forEach {
                    parent.findViewHolderForAdapterPosition(it)?.let { vh ->
                        paint.color = palette[array[it].color]
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
            setColor(it.bindingAdapterPosition, color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketItemViewHolder =
        MarketItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.market_item, parent, false
            ), this
        )

    override fun onBindViewHolder(holder: MarketItemViewHolder, position: Int) {
        holder.setItem(activity.state, array[position])
    }

    override fun getItemCount(): Int =
        array.size

    private fun getViewHolder(index: Int) =
        activity.rv_items.findViewHolderForAdapterPosition(index) as? MarketItemViewHolder

    fun add(item: MarketItem) {
        colorPreferences[item.name]?.let {
            item.color = it
        }

        val index = array.add(item)

        afterUpdate(index)
        notifyItemInserted(index)
    }

    fun removeAt(index: Int) {
        beforeUpdate(index)
        array.removeAt(index)
        notifyItemRemoved(index)
    }

    fun move(fromPosition: Int, toPosition: Int) {
        array.move(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun clear() {
        val sz = array.size
        array.clear()
        notifyItemRangeRemoved(0, sz)
    }

    fun setColor(index: Int, color: Int, shouldUpdateView: Boolean = true) {
        if(color == array[index].color)
            return

        array[index].color = color
        val i = array.orderChanged(index)

        if(shouldUpdateView)
            getViewHolder(index)?.setColor(color)

        notifyItemMoved(index, i)
    }

    fun setName(index: Int, name: String, shouldUpdateView: Boolean = true) {
        if(array[index].name == name)
            return

        array[index].name = name
        if(shouldUpdateView)
            getViewHolder(index)?.setName(name)

        colorPreferences[array[index].name]?.let {
            setColor(index, it)
        }
    }

    fun setAmount(index: Int, amount: String, shouldUpdateView: Boolean = false) {
        array[index].amount = amount
        if(shouldUpdateView)
            getViewHolder(index)?.setAmount(amount)
    }

    fun setWeight(index: Int, weight: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        array[index].weight = weight
        getViewHolder(index)?.apply{
            if(shouldUpdateView)
                setWeight(weight)
            setDiscrepancy(array[index].discrepancy)
        }
        afterUpdate(index)
    }

    fun setPrice(index: Int, price: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        array[index].price = price
        getViewHolder(index)?.apply {
            if(shouldUpdateView)
                setPrice(price)
            setDiscrepancy(array[index].discrepancy)
        }

        afterUpdate(index)
    }

    fun setCost(index: Int, cost: Float, shouldUpdateView: Boolean = false) {
        beforeUpdate(index)

        array[index].cost = cost
        getViewHolder(index)?.apply {
            if(shouldUpdateView)
                setCost(cost)
            setDiscrepancy(array[index].discrepancy)
        }

        afterUpdate(index)
    }

    fun setState(newState: MainActivity.State) {
        activity.rv_items.visibleViewHolders().forEach {
            if(it is MarketItemViewHolder)
                it.setState(newState, array[it.bindingAdapterPosition])
        }
    }

    private fun beforeUpdate(index: Int) {
        with(array[index]) {
            activity.summary.cost -= cost
            activity.summary.discrepancy -= discrepancy
        }
    }

    private fun afterUpdate(index: Int) {
        with(array[index]) {
            activity.summary.cost += cost
            activity.summary.discrepancy += discrepancy
        }
    }

    fun paste(s: String) {
        scope.launch {
            s.split(',', '\n')
                .filter(String::isNotBlank)
                .forEach {
                    val m = MarketItem.from(it)
                    activity.runOnUiThread {
                        add(m)
                    }
                }
        }
    }

    fun copy() = array.data.joinToString("\n", transform = MarketItem::toString)

    fun load() {
        if(palette.isEmpty())
            palette = activity.resources.getIntArray(R.array.user_palette)

        scope.launch {
            dbHelper.readableDatabase.use {
                dbHelper.readPreferences(colorPreferences::plusAssign)
                dbHelper.readSnapshot {
                    activity.runOnUiThread {
                        add(it)
                    }
                }
            }
            dbHelper.writableDatabase.use {

            }
        }
    }

    fun save() {
        dbHelper.clear()
        array.data.forEach {
            colorPreferences[it.name.lowercase(Locale.getDefault())] = it.color
        }
        dbHelper.writePreferences(colorPreferences)
        dbHelper.writeSnapshot(array.data)
        clear()
    }
}