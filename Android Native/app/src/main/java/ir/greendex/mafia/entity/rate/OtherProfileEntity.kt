package ir.greendex.mafia.entity.rate

import com.google.gson.annotations.SerializedName

data class OtherProfileEntity(
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("data")
    val data:OtherProfileData
){
    data class OtherProfileData(
        @SerializedName("data")
        val detail:OtherProfileDetailData
    ){
        data class OtherProfileDetailData(
            @SerializedName("idenity")
            val name:String,
            @SerializedName("avatar")
            val assets:OtherProfileAssetData,
            @SerializedName("points")
            val points:OtherProfilePointData,
            @SerializedName("games_result")
            val gamesResult:OtherProfileGameResultData
        )

        data class OtherProfileGameResultData(
            @SerializedName("game_as_mafia")
            val gameAsMafia:Int,
            @SerializedName("win_as_mafia")
            val winAsMafia:Int,
            @SerializedName("game_as_citizen")
            val gameAsCitizen:Int,
            @SerializedName("win_as_citizen")
            val winAsCitizen:Int
        )

        data class OtherProfilePointData(
            @SerializedName("win")
            val win:Int,
            @SerializedName("lose")
            val lose:Int,
            @SerializedName("abdon")
            val abandon:Int,
            @SerializedName("com_report")
            val comReport:Int,
            @SerializedName("role_report")
            val roleReport:Int

        )
        data class OtherProfileAssetData(
            @SerializedName("avatar")
            val avatar:String,
            @SerializedName("table")
            val anim:String
        )
    }
}
