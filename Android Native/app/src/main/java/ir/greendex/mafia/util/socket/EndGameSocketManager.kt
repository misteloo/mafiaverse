package ir.greendex.mafia.util.socket

import android.util.Log
import io.socket.client.Socket
import ir.greendex.mafia.game.general.listener.EndGameListener
import org.json.JSONObject
import javax.inject.Inject

class EndGameSocketManager @Inject constructor(private val socket: Socket) {
    companion object {
        const val END_GAME_FREE_SPEECH = "end_game_free_speech"
    }

    fun init(listener: EndGameListener) {
        socket.on(END_GAME_FREE_SPEECH) {
            Log.i("LOG", "end game speech: ${it[0]}")
            listener.onUserSpeech(it[0].toString())
        }

        addToList()
    }

    private fun addToList() {
        SocketManager.addEndGameResultSocketArray(name = END_GAME_FREE_SPEECH)
    }

    fun emit(obj: JSONObject) {
        socket.emit("game_handle", obj)
    }

    fun turnOffSocket() {
        SocketManager.clearEndGameResultSocketArray()
    }
}