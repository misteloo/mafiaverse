package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class CurrentSpeechEntity(
    @SerializedName("current")
    val currentUserId: String,
    @SerializedName("timer")
    val timer: Int,
    @SerializedName("has_next")
    val hasNext: Boolean
)
