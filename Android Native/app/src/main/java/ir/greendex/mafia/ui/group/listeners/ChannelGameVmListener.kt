package ir.greendex.mafia.ui.group.listeners

import ir.greendex.mafia.entity.channel.OnlineGameUpdatePreStartEntity

interface ChannelGameVmListener {
    fun onOnlineGameUpdate(data:List<OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData>)
    fun onCheckReady()
    fun onChannelGameStart()
}