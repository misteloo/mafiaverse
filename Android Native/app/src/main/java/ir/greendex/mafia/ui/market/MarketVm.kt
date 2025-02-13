package ir.greendex.mafia.ui.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.entity.SimpleResponse
import ir.greendex.mafia.entity.market.MarketEntity
import ir.greendex.mafia.entity.market.MarketGoldEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.USER_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketVm @Inject constructor(
    private val serverRepository: ServerRepository,
    private val localRepository: LocalRepository
) : ViewModel() {
    private var userToken: String? = null
    val avatarItemLiveData = MutableLiveData<List<MarketEntity.MarketDataItemsDetail>>()
    val animationItemLiveData = MutableLiveData<List<MarketEntity.MarketDataItemsDetail>>()
    val goldItemLiveData = MutableLiveData<List<MarketGoldEntity>>()
    val userGold = MutableLiveData<Int?>()


    private suspend fun getUserToken(): String {
        userToken =
            localRepository.getStoreData(USER_TOKEN, GetLocalRepositoryEnum.String) as String
        return userToken!!
    }

    fun connectToPayment() {
        MainActivity.connectToPayment()
    }

    fun purchaseItem(itemId: String, userToken: String, callback: (SimpleResponse?) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val obj = JsonObject().apply {
                addProperty("token",userToken)
                addProperty("item_id",itemId)
            }

            serverRepository.purchaseItem(obj).collect {
                callback(it)
            }
        }

    fun getMarketItems() = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token", userToken ?: getUserToken())
        }

        serverRepository.marketItems(body = obj).collect {
            it?.data?.items?.forEach { loop ->
                when (loop.type) {
                    "avatar" -> avatarItemLiveData.postValue(loop.items)
                    "animation" -> animationItemLiveData.postValue(loop.items)
                    "gold" -> {
                        val goldItem = mutableListOf<MarketGoldEntity>()
                        loop.items.forEachIndexed { index, golds ->
                            goldItem.add(
                                MarketGoldEntity(
                                    id = golds.id,
                                    off = golds.off > 0,
                                    offPercent = golds.off.toString(),
                                    lastPrice = if (golds.off > 0) golds.price.toString() else null,
                                    currentPrice = if (golds.off > 0) golds.afterOff.toString() else golds.price.toString(),
                                    srcImage = when (index) {
                                        0 -> R.drawable.gold
                                        1 -> R.drawable.golds
                                        else -> R.drawable.coin_pack
                                    },
                                    count = golds.gold,
                                )
                            )
                        }
                        goldItemLiveData.postValue(goldItem)
                    }
                }
            }
            // user gold
            userGold.postValue(it?.data?.userGold)
        }
    }

    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }


}