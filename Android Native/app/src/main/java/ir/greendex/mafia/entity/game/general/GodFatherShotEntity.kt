package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class GodFatherShotEntity(
    @SerializedName("availabel_users")
    val availableUsers: List<String>,
    @SerializedName("max")
    val maxCount: Int,
    @SerializedName("timer")
    val timer: Int
)
