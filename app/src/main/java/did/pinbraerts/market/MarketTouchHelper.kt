package did.pinbraerts.market

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

class MarketTouchHelper(private val adapter: BaseAdapter): ItemTouchHelper(object : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder) =
        makeMovementFlags(
            UP or DOWN,
            START or END
        )

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder1: RecyclerView.ViewHolder,
        viewHolder2: RecyclerView.ViewHolder
    ): Boolean {
        adapter.move(viewHolder1.adapterPosition, viewHolder2.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.remove(viewHolder.adapterPosition)
    }
})
