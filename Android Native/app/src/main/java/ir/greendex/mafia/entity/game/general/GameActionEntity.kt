package ir.greendex.mafia.entity.game.general

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameActionEntity(
    @SerializedName("data")
    var data: List<GameActionData>
):Parcelable {
    @Parcelize
    data class GameActionData(
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("user_index")
        val userIndex: Int,
        @SerializedName("user_status")
        val userStatus: UserGameStatus,
        @SerializedName("user_action")
        val userAction: UserGameAction
    ):Parcelable
    @Parcelize

    data class UserGameStatus(
        @SerializedName("is_connected")
        val isConnected: Boolean,
        @SerializedName("is_alive")
        val isAlive: Boolean,
        @SerializedName("is_talking")
        var isTalking: Boolean
    ):Parcelable
    @Parcelize
    data class UserGameAction(
        @SerializedName("like")
        val like: Boolean,
        @SerializedName("dislike")
        val dislike: Boolean,
        @SerializedName("challenge_request")
        val challengeRequest: Boolean,
        @SerializedName("hand_rise")
        val voteHandRise:Boolean,
        @SerializedName("accepted_challenge_request")
        val challengeAccept: Boolean,
        @SerializedName("target_cover_hand_rise")
        val targetCoverHandRise: Boolean,
        @SerializedName("target_covert_accepted")
        val targetCoverAccepted:Boolean
    ):Parcelable
}
