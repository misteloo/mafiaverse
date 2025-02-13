package ir.greendex.mafia.util.socket

import android.util.Log
import io.socket.client.Socket
import ir.greendex.mafia.ui.group.listeners.ChannelGameListener
import ir.greendex.mafia.ui.group.listeners.ChannelListener
import org.json.JSONObject
import javax.inject.Inject

class ChannelSocketManager @Inject constructor(private val socket: Socket) {
    private lateinit var channelListener: ChannelListener
    private lateinit var channelGameListener: ChannelGameListener

    companion object {
        private val TAG = "LOG"
        const val HANDLE = "channel_handle"
        private const val SEND_CHANNEL_MSG = "send_channel_msg"
        private const val ONLINE_GAME = "online_game"
        private const val ONLINE_GAME_UPDATE = "online_game_update"
        private const val ONLINE_GAME_PRE_START_UPDATE = "online_game_pre_start_update"
        private const val READY_CHECK = "ready_check"
        private const val READY_CHECK_STATUS = "ready_check_status"
        private const val START_CHANNEL_GAME = "start_channel_game"

    }

    fun init(listener: ChannelListener) {
        this.channelListener = listener

        // receive channel message
        socket.on(SEND_CHANNEL_MSG) {
            listener.onChannelMessageReceived(it[0].toString())
        }
        // is online game exist ?
        socket.on(ONLINE_GAME) {
            listener.onOnlineGame(it[0].toString())
        }
        // update online game
        socket.on(ONLINE_GAME_UPDATE) {
            listener.onUpdateOnlineGame(it[0].toString())
        }

        addSockets()
    }

    fun initGameListener(listener: ChannelGameListener) {
        this.channelGameListener = listener

        // online game update
        socket.on(ONLINE_GAME_PRE_START_UPDATE) {
            listener.onOnlineGameUpdate(it[0].toString())
        }

        // check ready status
        socket.on(READY_CHECK_STATUS) {
            Log.i(TAG, "ready check status: ${it[0]}")
            listener.onReadyCheckStatus(it[0].toString())
        }

        // check ready
        socket.on(READY_CHECK) {
            listener.onCheckReady()
        }

        socket.on(START_CHANNEL_GAME) {
            listener.onChannelGameStart()
        }

        addGameSocket()
    }

    private fun addGameSocket() {
        SocketManager.addChannelGameSocket(ONLINE_GAME_UPDATE)
        SocketManager.addChannelGameSocket(READY_CHECK)
        SocketManager.addChannelGameSocket(READY_CHECK_STATUS)
        SocketManager.addChannelGameSocket(START_CHANNEL_GAME)
    }

    private fun addSockets() {
        SocketManager.addChannelSocket(SEND_CHANNEL_MSG)
        SocketManager.addChannelSocket(ONLINE_GAME)
        SocketManager.addChannelSocket(ONLINE_GAME_PRE_START_UPDATE)
    }

    fun emit(obj: JSONObject) {
        socket.emit(HANDLE, obj)
    }

    fun startChannelGame(jsonObject: JSONObject) {
        socket.emit("start_channel_game", jsonObject)
    }
}