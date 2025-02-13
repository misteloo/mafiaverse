package ir.greendex.mafia.ui.market

interface MarketListener {
    fun onConsumeSucceed()
    fun onMessage(it:String)
}