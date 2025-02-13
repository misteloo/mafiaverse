package ir.greendex.mafia.entity.channel

import com.google.gson.annotations.SerializedName

data class ReadyCheckStatusEntity(
    @SerializedName("data")
    val data:List<ReadyCheckStatusData>
){
    data class ReadyCheckStatusData(
        @SerializedName("user_id")
        val userId:String,
        @SerializedName("ready_check")
        val readyCheck:Int
    )
}