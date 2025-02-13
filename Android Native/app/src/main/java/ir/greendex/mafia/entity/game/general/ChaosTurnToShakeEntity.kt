package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ChaosTurnToShakeEntity(
    @SerializedName("data")
    val data: ChaosTurnToShakeData
) {
    data class ChaosTurnToShakeData(
        @SerializedName("user_id")
        var userId: String? = null
    )
}
