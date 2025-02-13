package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class DayInquiryEntity(
    @SerializedName("data")
    val data:DayInquiryData
){
    data class DayInquiryData(
        @SerializedName("timer")
        val timer:Int,
        @SerializedName("msg")
        val msg:String?
    )
}
