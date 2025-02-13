package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class WhichUserRequestSpeechOptionEntity(
    @SerializedName("data")
    val data:WhichUserRequestSpeechOptionData
){
    data class WhichUserRequestSpeechOptionData(
        @SerializedName("requester_id")
        val requesterId:String,
        @SerializedName("timer")
        val timer:Int
    )
}
