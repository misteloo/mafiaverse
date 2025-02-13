package ir.greendex.mafia.entity.local

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalCharacterEntity(
    @SerializedName("data")
    val data: List<LocalCharacterDeck>
) : Parcelable {
    @Parcelize
    data class LocalCharacterDeck(
        @SerializedName("id")
        val id: Int,
        @SerializedName("side")
        val side: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("multi")
        val multi: Boolean,
        var count:Int = 1
    ) : Parcelable
}
