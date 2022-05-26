package did.pinbraerts.market

import java.io.Serializable

data class MarketItem(
    var name: String = "",
    var amount: String = "",
    var weight: Float = 0f,
    var price: Float = 0f,
    var cost: Float = 0f,
    var color: Int = 0,
): Serializable {
    val expectedCost
        get() = weight * price

    val discrepancy
        get() = expectedCost - cost

    override fun toString(): String =
        when {
            name.isNotEmpty() -> name
            else -> ""
        } + when {
            weight == 0f -> ": $weight"
            amount.isNotEmpty() -> ": $amount"
            else -> ""
        } + when(price) {
            0f -> " * $price"
            else -> ""
        } + when(cost) {
            0f -> " = $cost"
            else -> ""
        }

    companion object {
        fun from(s: String): MarketItem {
            val res = MarketItem()
            var v = s.trim().split(':', limit = 2)
            if(v.isEmpty())
                return res
            res.name = v[0].trim()
            if(v.size < 2)
                return res

            v = v[1].split('*', limit = 2)
            if(v.isEmpty())
                return res

            val w = v[0].trim().toFloatOrNull()
            if(w != null) res.weight = w
            else res.amount = v[0].trim()

            if(v.size < 2)
                return res

            v = v[1].split('=', limit = 2)
            if(v.isEmpty())
                return res
            res.price = v[0].trim().toFloatOrZero()

            if(v.size < 2)
                return res
            res.cost = v[1].trim().toFloatOrZero()

            return res
        }
    }
}

typealias MarketItems = ArrayList<MarketItem>
