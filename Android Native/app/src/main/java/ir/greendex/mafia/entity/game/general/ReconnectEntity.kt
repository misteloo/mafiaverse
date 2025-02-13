package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ReconnectEntity(
    @SerializedName("data")
    val data: ReconnectData
) {

    data class ReconnectData(
        @SerializedName("users_data")
        val usersData: List<InGameUsersDataEntity.InGameUserData>,
        @SerializedName("game_event")
        val gameEvent: String,
        @SerializedName("game_action")
        val userHistory: List<GameActionEntity.GameActionData>,
        @SerializedName("room_id")
        val roomId: String,
        @SerializedName("character")
        var character: String? = null,
        @SerializedName("join_type")
        val joinType: String,
        @SerializedName("roles")
        val roles: List<UsersCharacterEntity.UsersCharacterData>,
        @SerializedName("game_id")
        val gameId:String
    )
}
