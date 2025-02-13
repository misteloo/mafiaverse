package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ChaosUserSpeechEntity(
    @SerializedName("data")
    val data:List<ChaosUserSpeechData>
){
    data class ChaosUserSpeechData(
        @SerializedName("user_id")
        val userId:String,
        @SerializedName("talking")
        val talking:Boolean
    )
}
