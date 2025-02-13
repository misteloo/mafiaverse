package ir.greendex.mafia.entity.lucky_wheel

import com.google.gson.annotations.SerializedName

data class LuckyWheelStatusEntity(
    @SerializedName("data")
    val data:LuckyWheelStatusData
){
    data class LuckyWheelStatusData(
        @SerializedName("is_ready")
        val isReady:Boolean,
        @SerializedName("time_remain")
        val timerRemain:Long
    )
}
