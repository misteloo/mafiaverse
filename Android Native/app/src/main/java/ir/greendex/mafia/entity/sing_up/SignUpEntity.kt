package ir.greendex.mafia.entity.sing_up

import com.google.gson.annotations.SerializedName

data class SignUpEntity(
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("data")
    val data:Any,
    @SerializedName("msg")
    val msg:String
)
