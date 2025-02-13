package ir.greendex.mafia.ui.group.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.db.entity.MessageDbEntity
import ir.greendex.mafia.entity.channel.ChannelGameEntity
import ir.greendex.mafia.entity.channel.ChannelGameUpdateEntity
import ir.greendex.mafia.entity.channel.ChannelMessageEntity
import ir.greendex.mafia.entity.channel.SpecificChannelEntity
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.ui.group.listeners.ChannelListener
import ir.greendex.mafia.ui.group.listeners.ChannelVmListener
import ir.greendex.mafia.util.NOT_FOUND
import ir.greendex.mafia.util.socket.ChannelSocketManager
import ir.greendex.mafia.util.socket.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GroupVm @Inject constructor(
    private val serverRepository: ServerRepository,
    private val localRepository: LocalRepository,
    private val gson: Gson
) : ViewModel() {

    private lateinit var channelSocketManager: ChannelSocketManager
    private lateinit var channelId: String
    private lateinit var channelVmListener: ChannelVmListener


    fun setChannelSocket(channelSocketManager: ChannelSocketManager) {
        this.channelSocketManager = channelSocketManager
    }

    fun setChannelId(channelId: String) {
        this.channelId = channelId
    }

    fun getChannelOnlineGames() = viewModelScope.launch {
        val data = JsonObject().apply {
            addProperty("channel_id", channelId)
        }
        serverRepository.channelOnlineGames(body = data).collect {
            if (it != null) channelVmListener.onChannelGame(data = it.data)
        }
    }

    fun getSpecificChannel(
        id: String,
        userToken: String,
        response: (SpecificChannelEntity.SpecificChannelData) -> Unit
    ) = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token", userToken)
            addProperty("channel_id", id)
        }
        serverRepository.getSpecificChannel(body = obj).collect {
            if (it != null) {
                // callback
                response(it.data)
                // store to db
                if (it.data.content.isNotEmpty()) {
                    val list = mutableListOf<MessageDbEntity>()
                    it.data.content.forEach { item ->
                        list.add(
                            MessageDbEntity(
                                channelId = id,
                                messageId = item.msgId,
                                userId = item.userId,
                                userName = item.userName,
                                userImage = item.userImage,
                                message = item.msg,
                                messageType = item.msgType,
                                messageTime = item.time,
                                userState = item.userState,
                                responseMessage = NOT_FOUND,
                                responseUserId = NOT_FOUND
                            )
                        )
                    }
                    localRepository.insertAllMessage(messages = list)
                }

            }
        }
    }


    // get user gold for creating game
    fun userHasEnoughGold(
        entireGold: Int,
        token: String,
        callback: (Boolean) -> Unit
    ) =
        viewModelScope.launch {
            val obj = JsonObject().apply {
                addProperty("gold", entireGold)
                addProperty("token", token)
            }
            serverRepository.userHasEnoughGold(body = obj).collect {
                if (it != null) callback(it.status)
            }
        }

    fun initChannelSocket(callback: ChannelVmListener) {
        this.channelVmListener = callback
        channelSocketManager.init(object : ChannelListener {
            override fun onChannelMessageReceived(it: String) {
                val res = gson.fromJson(it, ChannelMessageEntity::class.java)
                callback.onChannelMessageReceived(data = res.data)

                // store in local db
                storeMessageToDb(message = res.data)
            }

            override fun onOnlineGame(it: String) {
                val res = gson.fromJson(it, ChannelGameEntity::class.java)
                callback.onChannelGame(data = res.data)
            }

            override fun onUpdateOnlineGame(it: String) {
                val res = gson.fromJson(it, ChannelGameUpdateEntity::class.java)
                callback.onUpdateOnlineGame(data = res.data)

            }
        })
    }

    private fun storeMessageToDb(message: ChannelMessageEntity.ChannelMessageData) {
        val msg = MessageDbEntity(
            channelId = channelId,
            messageId = message.messageId,
            userId = message.userId,
            userName = message.userName,
            userImage = NOT_FOUND,
            message = message.message,
            messageType = message.messageType,
            messageTime = message.messageTime,
            userState = message.userState,
            responseUserId = NOT_FOUND,
            responseMessage = NOT_FOUND
        )

        localRepository.insertMessage(msg)
    }


    fun getLocalMessage(
        offset: Int,
        callback: (List<ChannelMessageEntity.ChannelMessageData>) -> Unit
    ) =
        viewModelScope.launch {
            val list = mutableListOf<ChannelMessageEntity.ChannelMessageData>()
            val job = CoroutineScope(Dispatchers.IO).launch {
                localRepository.getMessage(channelId = channelId, offset = offset).collect {
                    it.forEach { item ->
                        list.add(
                            ChannelMessageEntity.ChannelMessageData(
                                messageId = item.messageId,
                                userId = item.userId,
                                userName = item.userName,
                                userImage = item.userImage,
                                message = item.message,
                                messageType = item.messageType,
                                messageTime = item.messageTime,
                                userState = item.userState
                            )
                        )
                    }
                }
            }
            job.join()
            callback(list)
        }

    fun getAllMessageSizeFromDb(callbackSize: (Int) -> Unit) =
        viewModelScope.launch {
            localRepository.getAllMessageSize(channelId = channelId).collect { size ->
                val pageSize = 15
                val divisionSize = size / pageSize
                val divisionRemaining = size % pageSize
                val result = divisionSize + if (divisionRemaining > 0) 1 else 0
                if (result > 0) {
                    callbackSize(result - 1)
                } else callbackSize(0)
            }

        }

    fun sendMessage(message: String) {
        val data = JSONObject().apply {
            put("msg_type", "normal")
            put("msg", message)
        }
        val op = JSONObject().apply {
            put("op", "send_channel_msg")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun joinToChannel(channelId: String) {
        val data = JSONObject().apply {
            put("channel_id", channelId)
        }
        val op = JSONObject().apply {
            put("op", "join_to_channel")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun createGame(entireGold: Int, moderator: Boolean) {
        val data = JSONObject().apply {
            put("entire_gold", entireGold)
            put("scenario", "nato")
            put("with_mod", moderator)
        }
        val op = JSONObject().apply {
            put("data", data)
            put("op", "create_game")
        }
        channelSocketManager.emit(obj = op)
    }

    fun joinToGame(gameId: String) {
        val data = JSONObject().apply {
            put("game_id", gameId)
        }
        val op = JSONObject().apply {
            put("data", data)
            put("op", "join_channel_game")
        }
        channelSocketManager.emit(obj = op)
    }

    fun leaveChannelGame(gameId: String) {
        val data = JSONObject().apply {
            put("game_id", gameId)
        }
        val op = JSONObject().apply {
            put("op", "leave_channel_game")
            put("data", data)
        }
        channelSocketManager.emit(obj = op)
    }

    fun exitFromCurrentChannel() {
        val op = JSONObject().apply {
            put("op", "leave_channel")
        }
        channelSocketManager.emit(obj = op)
    }

    fun turnOffChannelSocket() {
        SocketManager.clearChannelGameSocketArray()
        SocketManager.clearChannelSocketArray()
    }
}