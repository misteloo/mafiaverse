package ir.greendex.mafia.entity.channel

import com.google.gson.annotations.SerializedName

data class SearchChannelEntity(
    @SerializedName("data")
    val data: List<SearchChannelData>
) {
    data class SearchChannelData(
        @SerializedName("channel_id")
        val channelId: String,
        @SerializedName("channel_name")
        val channelName: String,
        @SerializedName("channel_image")
        val channelImage: String,
        @SerializedName("channel_cup")
        val cup: Int,
        @SerializedName("is_member")
        var isMember: Boolean,
        @SerializedName("pending_request")
        val pendingRequest: Boolean,
        @SerializedName("users")
        var users: Int,
        var loading: Boolean
    )
}
