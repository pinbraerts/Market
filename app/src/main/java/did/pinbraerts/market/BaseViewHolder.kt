package did.pinbraerts.market

import androidx.recyclerview.widget.RecyclerView
import android.view.View

abstract class BaseViewHolder(view: View): RecyclerView.ViewHolder(view) {
    open fun setColor(color: Int) { }
    open fun setName(name: String) { }
    open fun setAmount(amount: String) { }
    open fun setWeight(weight: Float) { }
    open fun setPrice(price: Float) { }
    open fun setCost(cost: Float) { }

    open fun setItem(position: Int, item: MarketItem) {
        setColor(item.color)
        setName(item.name)
        setAmount(item.amount)
        setWeight(item.weight)
        setPrice(item.price)
        setCost(item.cost)
    }
}
