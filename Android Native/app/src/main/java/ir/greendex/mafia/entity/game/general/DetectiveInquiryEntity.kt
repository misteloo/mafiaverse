package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class DetectiveInquiryEntity(
    @SerializedName("user_id")
    val userId:String,
    @SerializedName("inquiry")
    val inquiry:Boolean
)
