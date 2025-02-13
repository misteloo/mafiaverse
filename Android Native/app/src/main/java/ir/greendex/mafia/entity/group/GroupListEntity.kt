package ir.greendex.mafia.entity.group

import com.google.gson.annotations.SerializedName

data class GroupListEntity(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("data")
    val data: List<GroupListData>
) {
    data class GroupListData(
        @SerializedName("group_id")
        val groupId: String,
        @SerializedName("group_name")
        val groupName: String,
        @SerializedName("group_image")
        val groupImage: String,
        @SerializedName("unread_message")
        val unreadMessage: Int,
        @SerializedName("notification_status")
        val notificationStatus: Boolean
    )
}
