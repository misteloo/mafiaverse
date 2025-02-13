package ir.greendex.mafia.entity.sing_up

import com.google.gson.annotations.SerializedName

data class ConfirmCodeEntity(
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("msg")
    val msg:String,
    @SerializedName("data")
    val data:ConfirmCodeData
){
    data class ConfirmCodeData(
        @SerializedName("token")
        val token:String
    )
}
