package ir.greendex.mafia.entity

import com.google.gson.annotations.SerializedName

data class TestAudioKitEntity(
    @SerializedName("data")
    val data:TestAudioKitData
){
    data class TestAudioKitData(
        @SerializedName("token")
        val token: String
    )
}
