package ir.greendex.mafia.entity.profile

import com.google.gson.annotations.SerializedName

data class ProfileEntity(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val data: ProfileData,
) {
    data class ProfileData(
        @SerializedName("idenity")
        val identity: ProfileIdentity,
        @SerializedName("avatar")
        val activeAsset: ProfileAsset,
        @SerializedName("points")
        val points: ProfilePoints,
        @SerializedName("games_result")
        val gameResult: ProfileGameResult,
        @SerializedName("ranking")
        val ranking: ProfileRanking,
        @SerializedName("session_rank")
        val sessionRank: ProfileSessionRank,
    )

    data class ProfileIdentity(
        @SerializedName("name")
        val name: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("auth")
        val authorized: Boolean
    )

    data class ProfileAsset(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("table")
        val anim: String,
    )

    data class ProfilePoints(
        @SerializedName("win")
        val win: Int,
        @SerializedName("lose")
        val lose: Int,
        @SerializedName("abdon")
        val abandon: Int,
        @SerializedName("com_report")
        val communicationReport: Int,
        @SerializedName("role_report")
        val roleReport: Int
    )

    data class ProfileGameResult(
        @SerializedName("game_as_mafia")
        val gameAsMafia: Int,
        @SerializedName("win_as_mafia")
        val winAsMafia: Int,
        @SerializedName("game_as_citizen")
        val gameAsCitizen: Int,
        @SerializedName("win_as_citizen")
        val winAsCitizen: Int
    )

    data class ProfileRanking(
        @SerializedName("xp")
        val xp: Int,
        @SerializedName("rank")
        val rank: Int
    )

    data class ProfileSessionRank(
        @SerializedName("day")
        val day: Int,
        @SerializedName("week")
        val week: Int,
        @SerializedName("session")
        val session: Int
    )
}
