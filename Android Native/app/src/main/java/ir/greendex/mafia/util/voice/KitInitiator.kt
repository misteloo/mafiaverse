package ir.greendex.mafia.util.voice

class KitInitiator {

    companion object {
        private lateinit var voiceManager: VoiceManager

        fun setVoiceManager(voiceManager: VoiceManager) {
            this.voiceManager = voiceManager
        }


        val getVoiceManager get() = voiceManager
    }
}