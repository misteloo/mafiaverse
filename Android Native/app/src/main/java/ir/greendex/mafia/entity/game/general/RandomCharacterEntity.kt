package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class RandomCharacterEntity(
    @SerializedName("data")
    val data: RandomCharacterData,
    @SerializedName("scenario")
    val scenario: String
) {
    data class RandomCharacterData(
        @SerializedName("name")
        val name: String
    )
}
