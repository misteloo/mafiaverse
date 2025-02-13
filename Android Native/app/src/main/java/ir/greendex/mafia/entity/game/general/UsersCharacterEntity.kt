package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class UsersCharacterEntity(
    @SerializedName("data")
    val data:List<UsersCharacterData>
){
    data class UsersCharacterData(
        @SerializedName("user_id")
        val userId:String,
        @SerializedName("character")
        val character:String
    )
}
