package ir.greendex.mafia.entity.game.general

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class InGameUsersDataEntity(
    @SerializedName("data")
    val data: List<InGameUserData>
):Parcelable {
    @Parcelize
    data class InGameUserData(
        @SerializedName("user_name")
        val userName: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("index")
        val userIndex: Int,
        @SerializedName("user_image")
        val userImage: String,
        @SerializedName("user_anim")
        val userAnim: String
    ):Parcelable

}
