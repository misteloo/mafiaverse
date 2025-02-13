package ir.greendex.mafia.entity.transaction

import com.google.gson.annotations.SerializedName

data class TransactionEntity(
    @SerializedName("status")
    val status:Boolean,
    @SerializedName("data")
    val data: List<TransactionData>
) {
    data class TransactionData(
        @SerializedName("type")
        val type: String,
        @SerializedName("gold")
        val gold: Int,
        @SerializedName("price")
        val price: String,
        @SerializedName("date")
        val date: Long,
        @SerializedName("item")
        val item:String?=null,
        @SerializedName("device")
        val device: String,
        @SerializedName("note")
        val note: String
    )
}
