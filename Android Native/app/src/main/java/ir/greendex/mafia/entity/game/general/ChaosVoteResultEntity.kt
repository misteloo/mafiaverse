package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ChaosVoteResultEntity(
    @SerializedName("data")
    val data:ChaosVoteResultData
){
    data class ChaosVoteResultData(
        @SerializedName("from_user")
        val fromUser:String,
        @SerializedName("to_user")
        val toUser:String
    )
}
