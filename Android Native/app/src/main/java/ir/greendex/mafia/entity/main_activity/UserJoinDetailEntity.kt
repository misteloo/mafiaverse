package ir.greendex.mafia.entity.main_activity

import com.google.gson.annotations.SerializedName

data class UserJoinDetailEntity(
    @SerializedName("data")
    val data:UserJoinDetailData
){
    data class UserJoinDetailData(
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("auth")
        val auth: Boolean
    )
}
