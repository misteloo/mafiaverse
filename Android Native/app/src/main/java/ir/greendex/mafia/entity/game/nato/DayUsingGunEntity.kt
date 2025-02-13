package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName

data class DayUsingGunEntity(
    @SerializedName("data")
    val data:DayUsingGunData
){
    data class DayUsingGunData(
        @SerializedName("user_id")
        val userId:String
    )
}
