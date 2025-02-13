package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class GameEventEntity(
    @SerializedName("data")
    val data: GameEventData
) {
    data class GameEventData(
        @SerializedName("game_event")
        val gameEvent: String
    )
}
