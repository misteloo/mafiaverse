package ir.greendex.mafia.ui.group.listeners

interface ChannelGameListener {
    fun onOnlineGameUpdate(it:String)
    fun onCheckReady()
    fun onChannelGameStart()
    fun onReadyCheckStatus(it:String)
}