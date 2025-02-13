package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName

data class MafiaDecisionEntity(
    @SerializedName("nato_availabel")
    val natoAvailable: Boolean,
    @SerializedName("timer")
    val timer: Int
)
