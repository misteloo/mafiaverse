package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName

data class MafiaVisitationEntity(
    @SerializedName("data")
    val data: MafiaVisitationData
) {
    data class MafiaVisitationData(
        @SerializedName("mafia")
        val mafia: String
    )
}
