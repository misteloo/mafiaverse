package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class RequestSpeechOptionsEntity(
    @SerializedName("data")
    val data:RequestSpeechOptionsData
){
    data class RequestSpeechOptionsData(
        @SerializedName("requester_id")
        val requesterId:String,
        @SerializedName("option")
        val option:String,
        @SerializedName("timer")
        val timer:Int
    )
}
