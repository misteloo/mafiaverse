package ir.greendex.mafia.entity.rate

import com.google.gson.annotations.SerializedName

data class RankingEntity(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("data")
    val data: RankingTypeResultModel
) {
    data class RankingTypeResultModel(
        @SerializedName("ranking")
        val ranking: List<RankingModel>
    )

    data class RankingModel(
        @SerializedName("session")
        val session: String,
        @SerializedName("session_end")
        val sessionEnd: Long,
        @SerializedName("ranking_list")
        val dataList: List<RankingDataModel>,
        @SerializedName("user_self")
        val self: RankingDataModel
    )

    data class RankingDataModel(
        @SerializedName("idenity")
        val userIdentity: RankingIdentityModel,
        @SerializedName("avatar")
        val userAvatar: RankingAvatarModel,
        @SerializedName("session_rank")
        val sessionRank: Int,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("rate")
        val rate: Int,
        @SerializedName("prize")
        val prize:Int

    )

    data class RankingIdentityModel(
        @SerializedName("name")
        val userName: String
    )

    data class RankingAvatarModel(
        @SerializedName("avatar")
        val userImage: String,
        @SerializedName("tabel")
        val userAnim: String
    )
}
