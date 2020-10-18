package did.pinbraerts.market

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseViewHolder(view: View): RecyclerView.ViewHolder(view) {
    public open fun setColor(color: Int) { }
    public open fun setName(name: String) { }
    public open fun setAmount(amount: String) { }
    public open fun setWeight(weight: Float) { }
    public open fun setPrice(price: Float) { }
    public open fun setCost(cost: Float) { }

    public open fun setItem(position: Int, item: MarketItem) {
        setColor(item.color)
        setName(item.name)
        setAmount(item.amount)
        setWeight(item.weight)
        setPrice(item.price)
        setCost(item.cost)
    }
}
