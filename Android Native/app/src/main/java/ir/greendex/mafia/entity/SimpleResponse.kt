package ir.greendex.mafia.entity

import com.google.gson.annotations.SerializedName

data class SimpleResponse(
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("msg")
    val msg:String,
    @SerializedName("data")
    val data:Any
)
