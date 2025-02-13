package ir.greendex.mafia.entity.game.general

import com.google.gson.annotations.SerializedName

data class CharactersEntity(
    @SerializedName("data")
    val encryptedData: String
) {
    data class CharacterData(
        @SerializedName("name")
        val name: String,
        @SerializedName("selected")
        val selected: Boolean,
        @SerializedName("selected_by")
        val selectedBy: String,
        @SerializedName("id")
        val id: Int
    )
}
