package ir.greendex.mafia.game.vm.general

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.entity.game.general.EndGameFreeSpeechEntity
import ir.greendex.mafia.game.general.listener.EndGameListener
import ir.greendex.mafia.game.general.listener.EndGameVmListener
import ir.greendex.mafia.util.socket.EndGameSocketManager
import ir.greendex.mafia.util.voice.KitInitiator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GameResultVm @Inject constructor(
    private val gson: Gson
) : ViewModel() {
    private lateinit var endGameResultSocket: EndGameSocketManager
    private var micStatus = false
    private var voiceManager = KitInitiator.getVoiceManager
    fun initRoom(roomId: String) = viewModelScope.launch(Dispatchers.IO) {
        voiceManager.init(token = roomId)
    }

    val micStatusLiveData = MutableLiveData(false)
    fun setMicStatus(status: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val job = async { voiceManager.publishing(micStatus = status) }
        job.await()
        job.join()
        micStatusLiveData.postValue(status)
        micStatus = status
        // mic status to server
        MainActivity.userId?.let {
            setUserSpeaking(userId = it, talking = status)
        }
    }

    fun getMicStatus() = micStatus


    fun setSocket(endGameSocketManager: EndGameSocketManager) {
        this.endGameResultSocket = endGameSocketManager
    }

    fun initSocket(callback: EndGameVmListener) {
        endGameResultSocket.init(object : EndGameListener {
            override fun onUserSpeech(it: String) {
                val res = gson.fromJson(it, EndGameFreeSpeechEntity::class.java)
                callback.onSpeech(data = res.data)
            }
        })
    }

    private fun setUserSpeaking(userId: String, talking: Boolean) {
        val data = JSONObject().apply {
            put("user_id", userId)
            put("is_talking", talking)
        }
        val op = JSONObject().apply {
            put("data", data)
            put("op", "end_game_free_speech")
        }

        endGameResultSocket.emit(op)

    }

    fun turnOffSocket() {
        endGameResultSocket.turnOffSocket()
    }

    fun disConnectRoom() = viewModelScope.launch(Dispatchers.IO) {
        voiceManager.disconnect()
    }

}