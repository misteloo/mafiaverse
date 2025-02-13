package ir.greendex.mafia.entity.channel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ChannelGameEntity(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val data: List<ChannelGameData>
)

data class ChannelGameData(
    @SerializedName("game_id")
    val gameId: String,
    @SerializedName("creator_id")
    val creatorId: String,
    @SerializedName("scenario")
    val scenario: String,
    @SerializedName("entire_gold")
    val entireGold: Int,
    @SerializedName("observable")
    val observable: Boolean,
    @SerializedName("start_time")
    val startTime: Long,
    @SerializedName("end_time")
    val endTime: Long,
    @SerializedName("finished")
    val finished: Boolean,
    @SerializedName("started")
    val started:Boolean,
    @SerializedName("winner")
    val winner: String? = null,
    @SerializedName("users")
    var users: List<ChannelGameUserData>,
)

@Parcelize
data class ChannelGameUserData(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("name")
    val username: String,
    @SerializedName("image")
    val userImage: String,
    @SerializedName("side")
    val side: String? = null,
    @SerializedName("accepted")
    val accepted:Boolean
):Parcelable
