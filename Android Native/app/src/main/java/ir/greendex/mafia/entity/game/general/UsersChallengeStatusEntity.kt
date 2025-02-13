package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class UsersChallengeStatusEntity(
    @SerializedName("data")
    val data: List<UserChallengeStatusData>
) {
    data class UserChallengeStatusData(
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("status")
        val status: Boolean
    )
}
