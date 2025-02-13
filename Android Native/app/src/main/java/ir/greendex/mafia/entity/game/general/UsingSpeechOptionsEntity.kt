package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class UsingSpeechOptionsEntity(
    @SerializedName("data")
    val data:UsingSpeechOptionsData
){
    data class UsingSpeechOptionsData(
        @SerializedName("msg")
        val msg:String,
        @SerializedName("timer")
        val timer:Int
    )
}
