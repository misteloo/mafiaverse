package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class InGameModeratorEntity(
    @SerializedName("data")
    var data: InGameUsersDataEntity.InGameUserData? = null
)
