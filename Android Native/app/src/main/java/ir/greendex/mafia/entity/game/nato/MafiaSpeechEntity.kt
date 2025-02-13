package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName

data class MafiaSpeechEntity(
    @SerializedName("token")
    val token:String,
    @SerializedName("timer")
    val timer:Int,
    @SerializedName("teammate")
    val teammate:String
)
