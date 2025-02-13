package ir.greendex.mafia.entity.channel

import com.google.gson.annotations.SerializedName

data class ChannelGameUpdateEntity(
    @SerializedName("data")
    val data:ChannelGameUpdateData
){
    data class ChannelGameUpdateData(
        @SerializedName("users")
        val users:List<ChannelGameUserData>,
        @SerializedName("game_id")
        val gameId:String
    )
}
