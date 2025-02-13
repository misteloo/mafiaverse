package ir.greendex.mafia.entity.local

import com.google.gson.annotations.SerializedName

data class LocalBase64Entity(
    @SerializedName("data")
    val data:LocalBase64Data
){
    data class LocalBase64Data(
        @SerializedName("qr_code")
        val qr:String
    )
}
