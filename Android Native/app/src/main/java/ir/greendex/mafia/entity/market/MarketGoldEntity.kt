package ir.greendex.mafia.entity.market

data class MarketGoldEntity(
    var id: String,
    var off: Boolean = false,
    var offPercent:String,
    var lastPrice: String? = null,
    var currentPrice: String,
    var srcImage: Int,
    var count:Int
)
