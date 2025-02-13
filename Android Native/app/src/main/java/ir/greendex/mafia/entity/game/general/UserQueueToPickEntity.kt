package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class UserQueueToPickEntity(
    @SerializedName("data")
    val data: List<UserQueueToPickData>
) {
    data class UserQueueToPickData(
        @SerializedName("user_name")
        var userName: String,
        @SerializedName("user_id")
        var userId: String,
        @SerializedName("user_image")
        val userImage: String
    )
}
