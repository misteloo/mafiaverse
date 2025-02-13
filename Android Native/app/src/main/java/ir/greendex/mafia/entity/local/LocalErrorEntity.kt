package ir.greendex.mafia.entity.local

import com.google.gson.annotations.SerializedName

data class LocalErrorEntity(
    @SerializedName("data")
    val data:LocalErrorMsg
){
    data class LocalErrorMsg(
        @SerializedName("msg")
        val msg:String
    )
}
