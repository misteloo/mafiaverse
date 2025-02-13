package ir.greendex.mafia.ui.rate

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.entity.rate.OtherProfileEntity
import ir.greendex.mafia.entity.rate.RankingEntity
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.USER_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RankVm @Inject constructor(
    private val serverRepository: ServerRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private lateinit var selfDaily: RankingEntity.RankingDataModel
    private lateinit var selfWeekly: RankingEntity.RankingDataModel
    private lateinit var selfSeasonally: RankingEntity.RankingDataModel

    var rankDailyList = MutableLiveData<List<RankingEntity.RankingDataModel>>()
    var rankWeeklyList = MutableLiveData<List<RankingEntity.RankingDataModel>>()
    var rankSeasonallyList = MutableLiveData<List<RankingEntity.RankingDataModel>>()


    val getSelfDailyLiveData = MutableLiveData<RankingEntity.RankingDataModel>()
    val getSelfWeeklyLiveData = MutableLiveData<RankingEntity.RankingDataModel>()
    val getSelfSeasonallyLiveData = MutableLiveData<RankingEntity.RankingDataModel>()

    var dailyTimeLeft = -1L
    var weeklyTimeLeft = -1L
    var seasonallyTimeLeft = -1L

    val getRankingDataLiveData = MutableLiveData<RankingEntity>()

    fun checkUserProfile(userId:String,callback:(OtherProfileEntity?)->Unit) = viewModelScope.launch {
        MainActivity.userToken?.let {
            val obj = JsonObject().apply {
                addProperty("token",it)
                addProperty("user_id",userId)
            }
            serverRepository.otherProfile(obj).collect {
                callback(it)
            }
        }
    }
    fun storeRouting(route: Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER, route.name)
    }

    private suspend fun getUserToken(): String {
        return localRepository.getStoreData(USER_TOKEN, GetLocalRepositoryEnum.String) as String
    }

    fun getRankingData() = viewModelScope.launch {
        val obj = JsonObject().apply {
            addProperty("token", getUserToken())
        }
        serverRepository.ranking(obj).collect {
            if (it != null) {
                // parse data
                parseData(data = it.data.ranking)
                delay(500)
                getRankingDataLiveData.postValue(it)
            }
        }
    }

    suspend fun exportLeftTime(time: Float) = withContext(Dispatchers.IO) {
        val day = export(time / 86400f)
        val decimalDay = exportDecimal(time / 86400f)
        val hour = export(decimalDay * 24f)
        val decimalHour = exportDecimal(decimalDay * 24)
        val min = export(decimalHour * 60)
        val decimalMin = exportDecimal(decimalHour * 60)
        val sec = export(decimalMin * 60)
        return@withContext listOf(day, hour, min, sec)
    }

    private fun exportDecimal(num: Float): Float {
        return "0".plus(num.toString().substring(index(num), num.toString().length)).toFloat()
    }

    private fun export(num: Float): Int {
        return num.toString().substring(0, index(num)).toInt()
    }

    private fun index(num: Float): Int {
        return num.toString().indexOf('.')
    }

    private fun parseData(data: List<RankingEntity.RankingModel>) =
        viewModelScope.launch(Dispatchers.IO) {
            for (i in data.indices) {
                val item = data[i]
                when (i) {
                    0 -> {
                        rankDailyList.postValue(item.dataList)
                        dailyTimeLeft = item.sessionEnd
                        parseSelf(item = item.self, daily = true, week = false)
                    }

                    1 -> {
                        rankWeeklyList.postValue(item.dataList)
                        weeklyTimeLeft = item.sessionEnd
                        parseSelf(item = item.self, daily = false, week = true)
                    }

                    2 -> {
                        rankSeasonallyList.postValue(item.dataList)
                        seasonallyTimeLeft = item.sessionEnd
                        parseSelf(item = item.self, daily = false, week = false)
                    }
                }
            }
        }


    private fun parseSelf(
        item: RankingEntity.RankingDataModel,
        daily: Boolean,
        week: Boolean,
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (daily) {
            selfDaily = RankingEntity.RankingDataModel(
                userIdentity = item.userIdentity,
                userAvatar = item.userAvatar,
                sessionRank = item.sessionRank,
                userId = item.userId,
                rate = item.rate,
                prize = item.prize
            )
            getSelfDailyLiveData.postValue(selfDaily)
        } else if (week) {
            selfWeekly = RankingEntity.RankingDataModel(
                userIdentity = item.userIdentity,
                userAvatar = item.userAvatar,
                sessionRank = item.sessionRank,
                userId = item.userId,
                rate = item.rate,
                prize = item.prize
            )
            getSelfWeeklyLiveData.postValue(selfWeekly)
        } else {
            selfSeasonally = RankingEntity.RankingDataModel(
                userIdentity = item.userIdentity,
                userAvatar = item.userAvatar,
                sessionRank = item.sessionRank,
                userId = item.userId,
                rate = item.rate,
                prize = item.prize
            )
            getSelfSeasonallyLiveData.postValue(selfSeasonally)
        }
    }
}