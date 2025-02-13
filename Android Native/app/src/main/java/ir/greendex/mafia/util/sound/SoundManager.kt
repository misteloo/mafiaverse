package ir.greendex.mafia.util.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import ir.greendex.mafia.R
import ir.greendex.mafia.util.vibrate.Vibrate
import javax.inject.Inject

class SoundManager @Inject constructor(
    private val context: Context,
    private val audioManager: AudioManager,
    private val vibrate: Vibrate,
    private val mediaPlayer:MediaPlayer
) {
    init {
        @Suppress("DEPRECATION")
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }
    fun voiceCommand(id:Int){
        playCommandSound(id = id)
    }
    fun fromUrl(url: String) {
        playSoundFromUrl(url = url)
    }
    fun reportSound() {
        playSound(rawId = R.raw.report)
    }
    fun coinSound(){
        playSound(rawId = R.raw.coin_sound)
    }
    fun loseSound() {
        playSound(rawId = R.raw.lose)
    }

    fun winnerSound() {
        playSound(rawId = R.raw.success)
    }

    fun bellSound() {
        playSound(rawId = R.raw.church_bell_sound)
    }


    fun gameFoundSound() {
        playSound(rawId = R.raw.find_match_sound)
    }

    fun shotSound() {
        playSound(rawId = R.raw.pistol_sound)
    }




    private fun playSound(rawId: Int) {
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            // play audio source
            MediaPlayer.create(context, rawId).start()
            // vibration
            vibrate.longVibrate()
        }
    }

    private fun playCommandSound(id:Int){
        when(id){
            0 -> playSound(rawId = R.raw.voice_intro_day)
            1 -> playSound(rawId = R.raw.voice_next_player)
            2 -> playSound(rawId = R.raw.voice_mafia_revale)
            3 -> playSound(rawId = R.raw.voice_next_day)
            4 -> playSound(rawId = R.raw.voice_vote)
            else -> {}
        }
    }

    private fun playSoundFromUrl(url:String){
        if (mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
            return
        }
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }catch (_:Exception){}
    }

}