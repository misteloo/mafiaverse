package ir.greendex.mafia.entity.game_history

import com.google.gson.annotations.SerializedName

data class TotalGameHistoryEntity(
    @SerializedName("data")
    val data:List<GameTotalHistoryListEntity>
){
    data class GameTotalHistoryListEntity(
        @SerializedName("game_id")
        val gameId:String,
        @SerializedName("is_winner")
        val isWinner:Boolean,
        @SerializedName("winner")
        val winner:String,
        @SerializedName("point")
        val point:Int,
        @SerializedName("role")
        val role:String,
        @SerializedName("date")
        val date:Long
    )
}
