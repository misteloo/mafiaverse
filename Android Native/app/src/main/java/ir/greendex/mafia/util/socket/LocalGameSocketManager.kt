package ir.greendex.mafia.util.socket

import android.util.Log
import io.socket.client.Socket
import ir.greendex.mafia.ui.local_game.listeners.LocalGameListener
import org.json.JSONObject
import javax.inject.Inject

class LocalGameSocketManager @Inject constructor(
    private val socket: Socket
) {
    private lateinit var callback: LocalGameListener

    companion object {
        const val HANDLE = "handle_local_game"
        const val GET_DECK = "get_deck"
        const val LOCAL_GAME_STARTED = "local_game_started"
        const val USERS_JOIN = "users_join"
        const val ERROR = "error"
        const val PRV_DECK = "prv_deck"
        const val CART = "cart"
        const val USERS = "users"
    }

    fun init(listener: LocalGameListener) {
        this.callback = listener
        socket.on(GET_DECK) {
            callback.onGetDeck(it[0].toString())
        }

        socket.on(LOCAL_GAME_STARTED) {
            callback.onBase64(it[0].toString())
        }

        socket.on(USERS_JOIN) {
            callback.onUsersJoining(it[0].toString())
        }

        socket.on(ERROR) {
            callback.onError(it[0].toString())
        }

        socket.on(PRV_DECK) {
            callback.onCheckPrvDeck(it[0].toString())
        }

        // give a selected character to user
        socket.on(CART) {
            callback.onUserCharacter(it[0].toString())
        }

        socket.on(USERS) {
            callback.onUsersJoined(it[0].toString())
        }

        addArrays()
    }


    fun emit(obj: JSONObject) {
        socket.emit(HANDLE, obj)
    }

    fun emit(data: Any, event: String) {
        socket.emit(event, data)
    }

    fun turnOff() {
        SocketManager.clearLocalGameSocketArray()
    }

    private fun addArrays() {
        SocketManager.addLocalGameSocket(GET_DECK)
        SocketManager.addLocalGameSocket(LOCAL_GAME_STARTED)
        SocketManager.addLocalGameSocket(USERS_JOIN)
        SocketManager.addLocalGameSocket(ERROR)
        SocketManager.addLocalGameSocket(PRV_DECK)
        SocketManager.addLocalGameSocket(CART)
        SocketManager.addLocalGameSocket(USERS)
    }
}