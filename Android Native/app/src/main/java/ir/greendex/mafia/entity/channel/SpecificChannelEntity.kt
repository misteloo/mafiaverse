package ir.greendex.mafia.entity.channel

import com.google.gson.annotations.SerializedName
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity

data class SpecificChannelEntity(
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("data")
    val data:SpecificChannelData
){
    data class SpecificChannelData(
        @SerializedName("is_leader")
        val leader:Boolean,
        @SerializedName("is_co_leader")
        val coLeader:Boolean,
        @SerializedName("channel_name")
        val channelName:String,
        @SerializedName("channel_image")
        val channelImage:String,
        @SerializedName("channel_memeber")
        val channelMembers:Int,
        @SerializedName("channel_cup")
        val channelCup:Int,
        @SerializedName("content")
        val content:List<ChannelContent>
    ){
        data class ChannelContent(
            @SerializedName("msg_id")
            val msgId:String,
            @SerializedName("user_id")
            val userId:String,
            @SerializedName("user_name")
            val userName:String,
            @SerializedName("user_state")
            val userState:String,
            @SerializedName("user_image")
            val userImage:String,
            @SerializedName("msg")
            val msg:String,
            @SerializedName("msg_type")
            val msgType:String,
            @SerializedName("msg_time")
            val time:Long,
            @SerializedName("game_memebers")
            val gameMembers:List<InGameUsersDataEntity.InGameUserData>
        )
    }
}
