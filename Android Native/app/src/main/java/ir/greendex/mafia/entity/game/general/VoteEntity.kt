package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class VoteEntity(
    @SerializedName("data")
    val data: VoteData
) {
    data class VoteData(
        @SerializedName("user_id") // on vote user
        val userId: String,
        @SerializedName("timer")
        val timer:Int,
        @SerializedName("available_users") // which user can vote
        val availableUsers:List<String>
    )
}
