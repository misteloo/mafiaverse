package ir.greendex.mafia.entity.user

import com.google.gson.annotations.SerializedName

data class UserAuthentication(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val data: UserAuthenticationData
) {
    data class UserAuthenticationData(
        @SerializedName("url")
        val url: String
    )
}
