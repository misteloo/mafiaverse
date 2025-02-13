package ir.greendex.mafia.entity.channel

import com.google.gson.annotations.SerializedName

data class ChannelMessageEntity(
    @SerializedName("data")
    val data:ChannelMessageData
){
    data class ChannelMessageData(
        @SerializedName("msg_id")
        val messageId:String,
        @SerializedName("user_id")
        val userId:String,
        @SerializedName("user_name")
        val userName:String,
        @SerializedName("user_image")
        val userImage:String,
        @SerializedName("msg")
        val message:String,
        @SerializedName("msg_type")
        val messageType:String,
        @SerializedName("msg_time")
        val messageTime:Long,
        @SerializedName("user_state")
        val userState:String
    )
}
