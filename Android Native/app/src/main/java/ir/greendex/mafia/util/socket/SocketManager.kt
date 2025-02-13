package ir.greendex.mafia.util.socket

import android.util.Log
import com.google.gson.Gson
import io.socket.client.Socket
import ir.greendex.mafia.entity.game.general.ReconnectNotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject

class SocketManager {
    companion object {
        private const val JOIN_USER_TO_SERVER = "join"
        private const val JOIN_STATUS = "join_status"
        private const val FORCE_EXIT = "force_exit"
        private const val USER_GOLD = "user_gold"
        private const val APP_DETAIL = "app_detail"
        private lateinit var socket: Socket
        private lateinit var findMatchSocketManager: FindMatchSocketManager
        private lateinit var natoScenarioSocketManager: NatoScenarioSocketManager
        private lateinit var channelSocket: ChannelSocketManager
        private lateinit var endGameSocketManager: EndGameSocketManager
        private lateinit var localGameSocketManager: LocalGameSocketManager
        private lateinit var gson: Gson

        private val natoGameSocketList = mutableListOf<String>()
        private val channelSocketList = mutableListOf<String>()
        private val channelGameSocketList = mutableListOf<String>()
        private val endGameResultSocketList = mutableListOf<String>()
        private val localGameSocketList = mutableListOf<String>()
        fun setSocket(socket: Socket) {
            this.socket = socket
        }

        fun setGson(gson: Gson) {
            this.gson = gson
        }

        fun setFindMatchSocket(findMatchSocketManager: FindMatchSocketManager) {
            this.findMatchSocketManager = findMatchSocketManager
        }

        fun getFindMatchSocketManager() = findMatchSocketManager

        fun setNatoScenarioSocket(natoScenarioSocketManager: NatoScenarioSocketManager) {
            this.natoScenarioSocketManager = natoScenarioSocketManager
        }

        fun getNatoGameSocket() = natoScenarioSocketManager

        fun setChannelSocket(channelSocket: ChannelSocketManager) {
            this.channelSocket = channelSocket
        }

        fun getChannelSocketManager() = channelSocket

        fun setEndGameSocket(endGameSocketManager: EndGameSocketManager) {
            this.endGameSocketManager = endGameSocketManager
        }

        fun setLocalGameSocket(localGameSocket: LocalGameSocketManager) {
            this.localGameSocketManager = localGameSocket
        }

        val getLocalGameSocket get() = this.localGameSocketManager

        fun getEndGameSocket() = endGameSocketManager

        // BASICS
        fun connected() = socket.connected()
        fun connect() {
            try {
                socket.connect()
            } catch (_: Exception) {
            }
        }

        fun removeFindMatchSpecificSocket(name: String) {
            socket.off(name)
        }


        fun joinUserToServer(obj: JSONObject) {
            socket.emit(JOIN_USER_TO_SERVER, obj)
        }

        fun requestUserGold(token: String) {
            val obj = JSONObject().apply {
                put("token", token)
            }
            socket.emit(USER_GOLD, obj)
        }

        fun requestAppDetail() {
            socket.emit(APP_DETAIL)
        }

        fun userDetail(res: (String) -> Unit) {
            socket.on(JOIN_STATUS) {
                Log.i("LOG", "joinStatus: ${it[0]}")
                res(it[0].toString())
            }
        }

        fun userGold(res: (String) -> Unit) {
            socket.on(USER_GOLD) {
                Log.i("LOG", "user gold: ${it[0]}")
                res(it[0].toString())
            }
        }

        fun appDetail(res: (String) -> Unit) {
            socket.on(APP_DETAIL) {
                Log.i("LOG", "app detail: ${it[0]}")
                res(it[0].toString())
            }
        }


        fun forceExit(it: () -> Unit) {
            socket.on(FORCE_EXIT) {
                it()
            }
        }

        // reconnect to available game
        fun onReconnectToGameAvailable(data: (ReconnectNotificationEntity.ReconnectNotificationData) -> Unit) {
            socket.on("reconnect_notification") {

                val res = gson.fromJson(it[0].toString(), ReconnectNotificationEntity::class.java)
                // callback
                data(res.data)

            }
        }

        fun onReconnectReceivedData(data: (String) -> Unit) {
            socket.on("reconnect_data") {
                data(it[0].toString())
                socket.off("reconnect_data")
            }
        }

        fun reconnectToGame(gameId: String) {
            val data = JSONObject().apply {
                put("game_id", gameId)
            }
            socket.emit("reconnect", data)
        }

        fun abandonWithGameId(gameId: String) {
            val data = JSONObject().apply {
                put("game_id", gameId)
            }
            socket.emit("abandon", data)
        }

        fun abandonWithoutGameId() {
            socket.emit("abandon", null)
        }

        fun addNatoGameSocket(socketName: String) {
            natoGameSocketList.add(socketName)
        }

        fun addChannelSocket(name: String) {
            channelSocketList.add(name)
        }

        fun addChannelGameSocket(name: String) {
            channelGameSocketList.add(name)
        }

        fun addEndGameResultSocketArray(name: String) {
            endGameResultSocketList.add(name)
        }

        fun addLocalGameSocket(name: String) {
            localGameSocketList.add(name)
        }

        fun clearLocalGameSocketArray() {
            CoroutineScope(Dispatchers.IO).launch {
                localGameSocketList.asFlow()
                    .flowOn(Dispatchers.IO)
                    .onCompletion {
                        localGameSocketList.clear()
                    }.collect {
                        socket.off(it)
                    }
            }
        }

        fun clearNatoSocketGameArray() {
            natoGameSocketList.clear()
        }

        fun clearNatoGameSocket() {
            CoroutineScope(Dispatchers.IO).launch {
                natoGameSocketList.asFlow()
                    .flowOn(Dispatchers.IO)
                    .onCompletion {
                        clearNatoSocketGameArray()
                    }.collect {
                        socket.off(it)
                    }
            }
        }

        fun clearChannelGameSocketArray() {
            CoroutineScope(Dispatchers.IO).launch {
                channelGameSocketList.asFlow()
                    .flowOn(Dispatchers.IO)
                    .onCompletion {
                        channelGameSocketList.clear()
                    }.collect {
                        socket.off(it)
                    }
            }
        }

        fun clearChannelSocketArray() {
            CoroutineScope(Dispatchers.IO).launch {
                channelSocketList.asFlow().flowOn(Dispatchers.IO).onCompletion {
                    channelSocketList.clear()
                }.collect {
                    socket.off(it)
                }

            }
        }

        fun clearEndGameResultSocketArray() {
            CoroutineScope(Dispatchers.IO).launch {
                endGameResultSocketList.asFlow()
                    .flowOn(Dispatchers.IO)
                    .onCompletion {
                        endGameResultSocketList.clear()
                    }.collect {
                        socket.off(it)
                    }
            }
        }


        fun disconnectSocket() {
            socket.disconnect()
        }
    }
}