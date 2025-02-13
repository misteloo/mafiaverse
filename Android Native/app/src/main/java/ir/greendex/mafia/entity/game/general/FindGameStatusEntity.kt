package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class FindGameStatusEntity(

    @field:SerializedName("users")
    val users: List<FindGameStatusUsersData>
) {
    data class FindGameStatusUsersData(
        @field:SerializedName("device_id")
        val deviceId: String,
        @field:SerializedName("avatar")
        val avatar: String
    )
}