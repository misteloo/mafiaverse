package ir.greendex.mafia.ui.group.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.channel.OnlineGameUpdatePreStartEntity
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.ui.group.listeners.ChannelGameListener
import ir.greendex.mafia.ui.group.listeners.ChannelGameVmListener
import ir.greendex.mafia.util.socket.ChannelSocketManager
import ir.greendex.mafia.util.socket.SocketManager
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GroupGameVm @Inject constructor(
    private val gson: Gson,
    private val serverRepository: ServerRepository
) : ViewModel() {
    private lateinit var channelId: String
    private lateinit var channelSocketManager: ChannelSocketManager
    private lateinit var channelGameVmListener: ChannelGameVmListener
    fun setChannelSocket(channelSocketManager: ChannelSocketManager) {
        this.channelSocketManager = channelSocketManager
    }

    fun setChannelId(channelId: String) {
        this.channelId = channelId
    }

    // from api
    fun getOnlineGamePreStartUpdate(
        channelId: String,
        gameId: String
    ) =
        viewModelScope.launch {
            val obj = JsonObject().apply {
                addProperty("game_id", gameId)
                addProperty("channel_id", channelId)
            }
            serverRepository.onlineGamePreStartUpdate(obj).collect {
                if (it != null) channelGameVmListener.onOnlineGameUpdate(data = it.data)
            }
        }

    fun initChannelSocket(callback: ChannelGameVmListener) {
        this.channelGameVmListener = callback
        channelSocketManager.initGameListener(object : ChannelGameListener {
            override fun onOnlineGameUpdate(it: String) {
                val res = gson.fromJson(it, OnlineGameUpdatePreStartEntity::class.java)
                callback.onOnlineGameUpdate(data = res.data)
            }

            override fun onCheckReady() {
                callback.onCheckReady()
            }

            override fun onChannelGameStart() {
                callback.onChannelGameStart()
            }

            override fun onReadyCheckStatus(it: String) {
                Log.i("LOG", "onReadyCheckStatus: ${it[0]}")
            }
        })
    }

    fun confirmOrDeniUser(gameId: String, requesterId: String, accept: Boolean) {
        val data = JSONObject().apply {
            put("game_id", gameId)
            put("requester_id", requesterId)
            put("accept", accept)
        }
        val op = JSONObject().apply {
            put("op", "filter_channel_game_users")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun kickUserFromRegisterGame(gameId: String, userId: String) {
        val data = JSONObject().apply {
            put("game_id", gameId)
            put("user_id", userId)
        }
        val op = JSONObject().apply {
            put("op", "filter_channel_kick_user")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun leaveChannelGame(gameId: String){
        val data = JSONObject().apply {
            put("game_id", gameId)
        }
        val op = JSONObject().apply {
            put("op", "leave_channel_game")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun checkReady(gameId: String) {
        val data = JSONObject().apply {
            put("game_id", gameId)
        }
        val op = JSONObject().apply {
            put("op", "ready_check")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun readyCheckStatus(status: Boolean, gameId: String) {
        val data = JSONObject().apply {
            put("game_id", gameId)
            put("status", status)
        }
        val op = JSONObject().apply {
            put("op", "ready_check_status")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun startChannelGame(gameId:String) {
        val data = JSONObject().apply {
            put("game_id", gameId)
        }
        channelSocketManager.startChannelGame(data)
    }

    fun turnOffSockets() {
        SocketManager.clearChannelGameSocketArray()
    }
}