package ir.greendex.mafia.entity.channel

import com.google.gson.annotations.SerializedName

data class MyChannelsEntity(
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("data")
    val data:List<MyChannelsData>
){
    data class MyChannelsData(
        @SerializedName("channel_name")
        val channelName:String,
        @SerializedName("channel_id")
        val channelId:String,
        @SerializedName("channel_image")
        val channelImage:String,
        @SerializedName("members")
        val members:Int,
        @SerializedName("notification_status")
        val notificationStatus:Boolean,
        @SerializedName("unread_messages")
        val unreadMessage:Int,
        @SerializedName("online_users")
        val onlineUsers:Int,
        @SerializedName("pin_state")
        val pinState:Boolean
    )
}
