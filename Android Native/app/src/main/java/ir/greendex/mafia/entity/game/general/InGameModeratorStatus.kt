package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class InGameModeratorStatus(
    @SerializedName("data")
    val data:InGameModeratorStatusData
){
    data class InGameModeratorStatusData(
        @SerializedName("connected")
        val connected:Boolean,
        @SerializedName("speaking")
        val talking:Boolean
    )
}
