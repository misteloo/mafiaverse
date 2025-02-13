package ir.greendex.mafia.entity.group

import com.google.gson.annotations.SerializedName

data class GroupDetailEntity (
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("msg")
    val msg:String,
    @SerializedName("data")
    val data:GroupDetailData
){
    data class GroupDetailData(
        @SerializedName("group_rate")
        val rate:Int,
        @SerializedName("is_master")
        val isMaster:Boolean,
        @SerializedName("group_content")
        val content:String,
        @SerializedName("notif_status")
        val notificationStatus:Boolean,
        @SerializedName("users")
        var users:List<GroupDetailUsersData>? = null
    )
    data class GroupDetailUsersData(
        @SerializedName("user_name")
        val userName:String,
        @SerializedName("user_image")
        val userImage:String,
        @SerializedName("user_grade")
        val grade:String
    )
}