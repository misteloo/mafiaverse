package ir.greendex.mafia.util.socket

import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class FindMatchSocketManager @Inject constructor(
    private val socket: Socket,
) {
    companion object {
        const val TAG = "LOG"
        const val FIND_MATCH = "find_match"
        const val USER_JOINED_FIND_MATCH = "find_match"
        const val LEAVE_FIND_MATCH = "leave_find"
        const val GAME_FOUND = "game_found"
        const val CHARACTERS = "characters"
        const val RANDOM_CHARACTER = "random_character"
        const val SELECTED_CHARACTER = "selected_character"
        const val GAME_HANDLE = "game_handle"
        const val READY_TO_CHOOSE = "ready_to_choose"
        const val USERS_PICK_QUEUE = "users_turn"
        const val YOUR_TURN = "your_turn"
        const val ABANDON = "abandon"
    }

    fun findMatch(jsonObject: JSONObject) {
        socket.emit(FIND_MATCH, jsonObject)
    }

    fun normalRobotUserJoinedFindMatch(callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            // list of users joined to robot findMatch
            socket.on(USER_JOINED_FIND_MATCH) {
                callback(it[0].toString())
            }
        }
    }

    fun gameFound(callback: (String) -> Unit) {
        socket.on(GAME_FOUND) {
            callback(it[0].toString())
            // turn off socket listener
            SocketManager.removeFindMatchSpecificSocket(GAME_FOUND)
            // turn off socket listener
            SocketManager.removeFindMatchSpecificSocket(USER_JOINED_FIND_MATCH)
        }
    }

    fun getCharacters(callback: (String) -> Unit) {
        socket.on(CHARACTERS) {
            callback(it[0].toString())
        }
    }

    fun selectCharacter(obj: JSONObject) {
        socket.emit(GAME_HANDLE, obj)
    }

    fun randomCharacter(callback: (String) -> Unit) {
        socket.on(RANDOM_CHARACTER) {
            callback(it[0].toString())
            SocketManager.removeFindMatchSpecificSocket(RANDOM_CHARACTER)
        }
    }

    fun readyToChooseCharacter() {
        val obj = JSONObject().apply {
            put("op", READY_TO_CHOOSE)
        }
        socket.emit(GAME_HANDLE, obj)
    }

    fun userTurnToPick(callback: (String) -> Unit) {
        socket.on(USERS_PICK_QUEUE) {
            callback(it[0].toString())
        }
    }

    fun yourTurnToPick(callback: () -> Unit) {
        socket.on(YOUR_TURN) {
            callback()
        }
    }

    fun abandon(callback: () -> Unit) {
        socket.on(ABANDON) {
            callback()
            // clear
            removeBasicListener()
        }
    }

    fun removeBasicListener() {
        SocketManager.removeFindMatchSpecificSocket(RANDOM_CHARACTER)
        SocketManager.removeFindMatchSpecificSocket(CHARACTERS)
        SocketManager.removeFindMatchSpecificSocket(USERS_PICK_QUEUE)
        SocketManager.removeFindMatchSpecificSocket(YOUR_TURN)
        SocketManager.removeFindMatchSpecificSocket(ABANDON)
    }

    fun leaveFindMatch() {
        socket.emit(LEAVE_FIND_MATCH)
        SocketManager.removeFindMatchSpecificSocket(USER_JOINED_FIND_MATCH)
        SocketManager.removeFindMatchSpecificSocket(GAME_FOUND)
    }

}