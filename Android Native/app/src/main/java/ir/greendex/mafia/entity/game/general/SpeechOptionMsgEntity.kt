package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class SpeechOptionMsgEntity(
    @SerializedName("data")
    val data:SpeechOptionMsgData
){
    data class SpeechOptionMsgData(
        @SerializedName("msg")
        val msg:String,
        @SerializedName("timer")
        val timer:Int
    )
}
