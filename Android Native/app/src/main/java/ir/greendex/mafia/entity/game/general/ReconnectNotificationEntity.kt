package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ReconnectNotificationEntity(
    @SerializedName("data")
    val data: ReconnectNotificationData
) {
    data class ReconnectNotificationData(
        @SerializedName("is_player")
        val isPlayer: Boolean,
        @SerializedName("is_supervisor")
        val isSupervisor: Boolean,
        @SerializedName("game_scenario")
        val gameScenario: String,
        @SerializedName("game_id")
        val gameId: String,
        @SerializedName("character")
        val character: String? = null
    )
}
