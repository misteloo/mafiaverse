package ir.greendex.mafia.entity.local

import com.google.gson.annotations.SerializedName

data class LocalUsersJoinedEntity(
    @SerializedName("data")
    val data: LocalJoinedUsersData
) {
    data class LocalJoinedUsersData(
        @SerializedName("users")
        val users: List<LocalJoinedUsersDetail>
    )

    data class LocalJoinedUsersDetail(
        @SerializedName("cart")
        val userRole: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("name")
        val userName: String
    )
}
