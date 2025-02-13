package ir.greendex.mafia.entity.channel

import com.google.gson.annotations.SerializedName

data class OnlineGameUpdatePreStartEntity(
    @SerializedName("data")
    val data:List<OnLineGameUpdatePreStartData>
){
    data class OnLineGameUpdatePreStartData(
        @SerializedName("user_id")
        val userId:String,
        @SerializedName("name")
        val userName:String,
        @SerializedName("image")
        val userImage:String,
        @SerializedName("accepted")
        val accepted:Boolean,
        @SerializedName("is_ready")
        var isReady:Int = -1 // -1 idle , 0 not ready , 1 ready
    )
}
