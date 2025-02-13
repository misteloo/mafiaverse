package ir.greendex.mafia.ui.group.listeners

import ir.greendex.mafia.entity.channel.ChannelGameData
import ir.greendex.mafia.entity.channel.ChannelGameUpdateEntity
import ir.greendex.mafia.entity.channel.ChannelMessageEntity

interface ChannelVmListener {
    fun onChannelMessageReceived(data: ChannelMessageEntity.ChannelMessageData)
    fun onChannelGame(data:List<ChannelGameData>)
    fun onUpdateOnlineGame(data:ChannelGameUpdateEntity.ChannelGameUpdateData)
}