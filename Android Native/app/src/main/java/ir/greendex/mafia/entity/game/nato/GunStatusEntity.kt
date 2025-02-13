package ir.greendex.mafia.entity.game.nato

import com.google.gson.annotations.SerializedName

data class GunStatusEntity(
    @SerializedName("data")
    val data:GunStatusData
){
    data class GunStatusData(
        @SerializedName("gun_enable")
        val isAvailable:Boolean
    )
}
