package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class GameFoundEntity(
    @field:SerializedName("data")
    val data: GameFoundData
){
    data class GameFoundData(
        @SerializedName("is_creator")
        val isCreator:Boolean,
        @SerializedName("game_id")
        val gameId:String
    )
}