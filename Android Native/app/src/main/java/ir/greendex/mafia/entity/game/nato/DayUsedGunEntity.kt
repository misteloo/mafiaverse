package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName

data class DayUsedGunEntity(
    @SerializedName("data")
    val data:DayUsedGunData
){
    data class DayUsedGunData(
        @SerializedName("from_user")
        val fromUser:String,
        @SerializedName("to_user")
        val toUser:String,
        @SerializedName("kind")
        val kind:String
    )
}
