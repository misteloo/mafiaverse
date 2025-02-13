package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName
import ir.greendex.mafia.entity.game.nato.NatoCharacters

data class NatoMafiaVisitationEntity(
    @SerializedName("index")
    val index:Int,
    @SerializedName("role")
    val role:NatoCharacters,
    @SerializedName("user_id")
    val userId:String
)
