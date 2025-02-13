package ir.greendex.mafia.entity.market

import com.google.gson.annotations.SerializedName

data class MarketEntity(
    @SerializedName("data")
    val data:MarketData
){
    data class MarketData(
        @SerializedName("items")
        val items:List<MarketDataItems>,
        @SerializedName("user_gold")
        val userGold:Int
    )

    data class MarketDataItems(
        @SerializedName("type")
        val type:String,
        @SerializedName("items")
        val items:List<MarketDataItemsDetail>
    )

    data class MarketDataItemsDetail(
        @SerializedName("_id")
        val itemId:String,
        @SerializedName("id")
        val id:String,
        @SerializedName("price")
        val price:Int,
        @SerializedName("image")
        val image:String,
        @SerializedName("file")
        val anim:String,
        @SerializedName("active")
        val active:Boolean,
        @SerializedName("active_for_user")
        val activeForUser:Boolean,
        @SerializedName("off")
        val off:Int,
        @SerializedName("gold")
        val gold:Int,
        @SerializedName("price_after_off")
        val afterOff:Int
    )
}
