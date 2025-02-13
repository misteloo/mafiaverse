package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ModeratorLogEntity(
    @SerializedName("data")
    val data: ModeratorLogData
) {
    data class ModeratorLogData(
        @SerializedName("msg_type")
        val msgType: String, // server or event
        @SerializedName("header_msg")
        val headerMsg: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("content")
        val content: ModeratorLogContent
    ) {
        data class ModeratorLogContent(
            @SerializedName("from")
            val from: ModeratorLogContentUserData,
            @SerializedName("to")
            val to: List<ModeratorLogContentUserData>
        ) {
            data class ModeratorLogContentUserData(
                @SerializedName("user_id")
                val userId:String,
                @SerializedName("user_name")
                val userName:String,
                @SerializedName("user_index")
                val userIndex:Int,
                @SerializedName("user_image")
                val userImage:String,
                @SerializedName("character")
                val character:String
            )
        }
    }
}
