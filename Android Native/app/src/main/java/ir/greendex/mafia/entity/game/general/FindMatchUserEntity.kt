package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class FindMatchUserEntity(
    @SerializedName("data")
    val data: List<Data>
) {
    data class Data(
        @SerializedName("user_image")
        val userImage: String,
        @SerializedName("user_id")
        val userId: String
    )
}
