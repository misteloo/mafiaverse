package ir.greendex.mafia.entity.main_activity

import com.google.gson.annotations.SerializedName

data class AppDetailEntity(
    @SerializedName("data")
    val data: AppDetailData
) {
    data class AppDetailData(
        @SerializedName("v")
        val version: String,
        @SerializedName("server_update")
        val serverUpdate: Boolean,
    )
}
