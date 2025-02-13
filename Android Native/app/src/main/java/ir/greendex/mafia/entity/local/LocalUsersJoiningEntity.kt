package ir.greendex.mafia.entity.local

import com.google.gson.annotations.SerializedName

data class LocalUsersJoiningEntity(
    @SerializedName("data")
    val data:UsersJoinedToLocalData
){
    data class UsersJoinedToLocalData(
        @SerializedName("users")
        val users:List<UsersJoinedToLocalUser>,
        @SerializedName("can_start")
        val canStarted:Boolean
    ){
        data class UsersJoinedToLocalUser(
            @SerializedName("user_id")
            val userId:String,
            @SerializedName("name")
            val userName:String
        )
    }
}
