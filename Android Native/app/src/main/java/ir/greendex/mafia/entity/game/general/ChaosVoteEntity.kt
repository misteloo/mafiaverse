package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ChaosVoteEntity(
    @SerializedName("data")
    val data:ChaosVoteData
){
    data class ChaosVoteData(
        @SerializedName("available_users")
        val availableUsers:List<String>,
        @SerializedName("timer")
        val timer:Int
    )
}
