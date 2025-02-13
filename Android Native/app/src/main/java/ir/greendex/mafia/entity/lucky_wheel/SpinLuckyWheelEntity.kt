package ir.greendex.mafia.entity.lucky_wheel

import com.google.gson.annotations.SerializedName

data class SpinLuckyWheelEntity(
    @SerializedName("data")
    val data:SpinLuckyWheelData
){
    data class SpinLuckyWheelData(
        @SerializedName("percent")
        val percent:Int,
        @SerializedName("next_spin")
        val nextSpin:Long
    )
}
