package ir.greendex.mafia.entity.edit_profile

import com.google.gson.annotations.SerializedName

data class UserItemsEntity(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("data")
    val data: UserItemData
) {
    data class UserItemData(
        @SerializedName("items")
        val items: List<UserItemsList>
    ) {
        data class UserItemsList(
            @SerializedName("_id")
            val _id:String,
            @SerializedName("id")
            val id:String,
            @SerializedName("image")
            val image:String,
            @SerializedName("file")
            val file:String,
            @SerializedName("type")
            val type:String,
            @SerializedName("active")
            var active:Boolean = false
        )
    }
}
