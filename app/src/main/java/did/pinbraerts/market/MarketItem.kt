package did.pinbraerts.market

data class MarketItem(
    public var name: String = "",
    public var amount: String = "",
    public var weight: Float = 0f,
    public var price: Float = 0f,
    public var cost: Float = 0f,
    public var color: Int = 0,
) {
    public val expectedCost
        get() = weight * price

    public val discrepancy
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
            if(v.size == 0)
                return res
            if(v.size > 0)
                res.name = v[0].trim()
            if(v.size > 1)
                res.amount = v[1].trim()
            return res
        }

        fun deserialize(s: String): MarketItem {
            val a = s.split('汉')
            return MarketItem(
                name = a.elementAtOrElse(0, { "" }).trim(),
                weight = a.elementAtOrElse(1, { "" }).toFloatOrZero(),
                price = a.elementAtOrElse(2, { "" }).toFloatOrZero(),
                cost = a.elementAtOrElse(3, { "" }).toFloatOrZero(),
                amount = a.elementAtOrElse(4, { "" }).trim(),
                color = a.elementAtOrElse(5, { "" }).toIntOrZero(),
            )
        }
    }
}
