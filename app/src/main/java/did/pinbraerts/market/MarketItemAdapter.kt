package did.pinbraerts.market

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class MarketItemAdapter(
    val data: ArrayList<String>,
    val state: WeakReference<MainActivity.State>,
): RecyclerView.Adapter<MarketItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketItemViewHolder =
        MarketItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.market_item, parent, false
            ), state.get()!!
        )

    override fun onBindViewHolder(holder: MarketItemViewHolder, position: Int) {

    }

    override fun getItemCount(): Int =
        data.size
}