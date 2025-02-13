package ir.greendex.mafia.entity.game.general

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class EndGameResultEntity(
    @SerializedName("data")
    val data: EndGameResultData
) {
    @Parcelize
    data class EndGameResultData(
        @SerializedName("winner")
        val winner: String,
        @SerializedName("users")
        val users: List<EndGameResultUsers>,
        @SerializedName("scenario")
        val scenario: String,
        @SerializedName("free_speech_timer")
        val speechTimer: Long
    ) : Parcelable

    @Parcelize
    data class EndGameResultUsers(
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("user_name")
        val userName: String,
        @SerializedName("user_image")
        val userImage: String,
        @SerializedName("point")
        val point: Int,
        @SerializedName("role")
        val role: String,
        @SerializedName("side")
        val side: String,
        @SerializedName("xp")
        val xp: Int,
        @SerializedName("winner")
        val winner: Boolean,
        @SerializedName("gold")
        val gold:Int,
        var talking:Boolean = false
    ) : Parcelable
}
