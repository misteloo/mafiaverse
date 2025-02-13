package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class ReportEntity(
    @SerializedName("data")
    val data: ReportData
) {
    data class ReportData(
        @SerializedName("report_type")
        val reportType: String,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("user_id")
        val userId: String?,
        @SerializedName("timer")
        val timer: Int
    )
}
