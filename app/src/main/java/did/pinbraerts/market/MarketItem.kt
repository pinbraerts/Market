package did.pinbraerts.market

data class MarketItem(
    var name: String = "",
    var amount: String = "",
    var weight: Float = 0f,
    var price: Float = 0f,
    var cost: Float = 0f,
    var color: Int = 0,
) {
    val expectedCost
        get() = weight * price

    val discrepancy
        get() = expectedCost - cost

    fun serialize() =
        "${name}汉${weight}汉${price}汉${cost}汉${amount}汉${color}"

    fun toClipboard() =
        "$name : $weight * $price = $cost"

    fun isValid() =
        name.isNotBlank() or
        amount.isNotBlank() or
        (weight != 0f) or
        (price != 0f) or
        (cost != 0f)

    companion object {
        fun fromClipboard(s: String): MarketItem {
            val v = s.trim().split(':')
            val res = MarketItem()
            if(v.isEmpty())
                return res
            res.name = v[0].trim()
            if(v.size > 1)
                res.amount = v[1].trim()
            return res
        }

        fun deserialize(s: String): MarketItem {
            val a = s.split('汉')
            return MarketItem(
                name = a.elementAtOrElse(0) { "" }.trim(),
                weight = a.elementAtOrElse(1) { "" }.toFloatOrZero(),
                price = a.elementAtOrElse(2) { "" }.toFloatOrZero(),
                cost = a.elementAtOrElse(3) { "" }.toFloatOrZero(),
                amount = a.elementAtOrElse(4) { "" }.trim(),
                color = a.elementAtOrElse(5) { "" }.toIntOrZero(),
            )
        }
    }
}
