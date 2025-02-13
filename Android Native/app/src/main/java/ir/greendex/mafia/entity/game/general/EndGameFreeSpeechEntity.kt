package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class EndGameFreeSpeechEntity (
    @SerializedName("data")
    val data:List<EndGameFreeSpeechData>
){
    data class EndGameFreeSpeechData(
        @SerializedName("user_id")
        val userId:String,
        @SerializedName("is_talking")
        val talking:Boolean
    )
}