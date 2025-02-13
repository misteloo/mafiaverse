package ir.greendex.mafia.entity.local

import com.google.gson.annotations.SerializedName

data class LocalUserCharacterEntity(
    @SerializedName("data")
    val data:LocalCharacterEntity.LocalCharacterDeck
)
