package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class SpeechEndEntity(
    @SerializedName("data")
    val data: SpeechEndData
) {
    data class SpeechEndData(
        @SerializedName("user_id")
        val userId: String
    )
}
