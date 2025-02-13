package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class VoiceRoomEntity(
    @SerializedName("token")
    val token:String
)
