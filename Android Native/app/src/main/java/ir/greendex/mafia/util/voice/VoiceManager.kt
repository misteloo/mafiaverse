package ir.greendex.mafia.util.voice

import io.livekit.android.events.collect
import io.livekit.android.room.Room
import ir.greendex.mafia.util.AUDIO_KIT_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoiceManager(private val room: Room) {

    suspend fun init(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                room.connect(AUDIO_KIT_URL, token)
                startListening()
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun startListening() {
        CoroutineScope(Dispatchers.IO).launch {
            val localParticipant = room.localParticipant

            localParticipant.events.collect {
                getLocalParticipantAudioLevel(it.participant.audioLevel)
            }
        }
    }

    suspend fun publishing(micStatus: Boolean) {
        room.localParticipant.setMicrophoneEnabled(micStatus)
    }

    private var onParticipantSpeaking: ((Boolean) -> Unit)? = null
    fun onParticipantSpeakingCallback(it: (Boolean) -> Unit) {
        onParticipantSpeaking = it
    }

    private fun getLocalParticipantAudioLevel(level: Float) {
        onParticipantSpeaking?.let {
            it(level > 0.0f)
        }
    }

    fun disconnect() {
        try {
            room.disconnect()
        } catch (_: Exception) { }
    }

}