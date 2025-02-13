package ir.greendex.mafia.entity.local

import com.google.gson.annotations.SerializedName

data class LocalPrvDeckEntity(
    @SerializedName("data")
    val data:PrvDeckData
){
    data class PrvDeckData(
        @SerializedName("deck")
        val deckList:List<LocalCharacterEntity.LocalCharacterDeck>
    )
}
