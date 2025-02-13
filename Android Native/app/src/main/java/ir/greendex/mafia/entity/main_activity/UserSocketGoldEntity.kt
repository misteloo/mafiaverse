package ir.greendex.mafia.entity.main_activity

import com.google.gson.annotations.SerializedName

data class UserSocketGoldEntity(
    @SerializedName("data")
    val data:UserSocketGoldData
){
    data class UserSocketGoldData(
        @SerializedName("gold")
        val goldCount: Int
    )
}
