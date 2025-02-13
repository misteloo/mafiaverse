package ir.greendex.mafia.util.socket

import io.socket.client.Socket
import javax.inject.Inject

class SocketInitiator @Inject constructor(
    private val socket: Socket,
    private val natoScenarioSocketManager: NatoScenarioSocketManager,
    private val findMatchSocketManager: FindMatchSocketManager,
    private val channelSocket: ChannelSocketManager,
    private val endGameSocketManager: EndGameSocketManager,
    private val localGameSocketManager: LocalGameSocketManager
) {

    val getSocket get() = socket
    val getNatoScenarioSocket get() = natoScenarioSocketManager
    val getFindMatchSocket get() = findMatchSocketManager
    val getChannelSocket get() = channelSocket
    val getEndGameSocket get() = endGameSocketManager
    val getLocalGameSocket get() = localGameSocketManager
}