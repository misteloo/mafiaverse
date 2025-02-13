package ir.greendex.mafia.entity.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalSelectDeckEntity(
    val id: Int,
    val side: String,
    val icon: String,
    val name: String,
    val description: String,
    val multi: Boolean,
    var selected: Boolean,
    var count: Int = 0
) : Parcelable
