package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class PlayVoiceEntity(
    @SerializedName("data")
    val data: PlayVoiceDataEntity
) {
    data class PlayVoiceDataEntity(
        @SerializedName("voice_id")
        val id: Int
    )
}
