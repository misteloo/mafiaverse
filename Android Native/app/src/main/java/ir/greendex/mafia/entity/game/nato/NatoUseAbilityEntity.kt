package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName

data class NatoUseAbilityEntity(
    @SerializedName("data")
    val data: NatoUseAbilityData
) {
    data class NatoUseAbilityData(
        @SerializedName("max_count")
        val maxCount: Int,
        @SerializedName("availabel_users")
        val availableUsers: List<String>,
        @SerializedName("can_act")
        val canAct: Boolean,
        @SerializedName("timer")
        val timer: Int,
        @SerializedName("msg")
        val msg: String
    )
}
