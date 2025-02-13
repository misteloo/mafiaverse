package ir.greendex.mafia.ui.group.listeners

interface ChannelListener {
    fun onChannelMessageReceived(it:String)
    fun onOnlineGame(it:String)
    fun onUpdateOnlineGame(it:String)
}