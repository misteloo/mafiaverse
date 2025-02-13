package ir.greendex.mafia.repository.server

import android.util.Log
import com.google.gson.JsonObject
import dagger.hilt.android.scopes.ActivityRetainedScoped
import ir.greendex.mafia.entity.SimpleResponse
import ir.greendex.mafia.entity.TestAudioKitEntity
import ir.greendex.mafia.entity.channel.ChannelGameEntity
import ir.greendex.mafia.entity.channel.MyChannelsEntity
import ir.greendex.mafia.entity.channel.OnlineGameUpdatePreStartEntity
import ir.greendex.mafia.entity.channel.SearchChannelEntity
import ir.greendex.mafia.entity.channel.SpecificChannelEntity
import ir.greendex.mafia.entity.lucky_wheel.LuckyWheelStatusEntity
import ir.greendex.mafia.entity.lucky_wheel.SpinLuckyWheelEntity
import ir.greendex.mafia.entity.market.MarketEntity
import ir.greendex.mafia.entity.profile.ProfileEntity
import ir.greendex.mafia.entity.edit_profile.UserItemsEntity
import ir.greendex.mafia.entity.game_history.TotalGameHistoryEntity
import ir.greendex.mafia.entity.rate.OtherProfileEntity
import ir.greendex.mafia.entity.rate.RankingEntity
import ir.greendex.mafia.entity.sing_up.ConfirmCodeEntity
import ir.greendex.mafia.entity.sing_up.SignUpEntity
import ir.greendex.mafia.entity.transaction.TransactionEntity
import ir.greendex.mafia.entity.user.UserAuthentication
import ir.greendex.mafia.entity.user.UserGoldEntity
import ir.greendex.mafia.repository.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@ActivityRetainedScoped
class ServerRepository @Inject constructor(
    private val api: Api
) {
    companion object {
        const val TAG = "LOG"
    }

    fun testAudioKit(uId: String, data: (TestAudioKitEntity) -> Unit) {
        val obj = JsonObject().apply {
            addProperty("name", uId)
        }
        api.testKit(obj).enqueue(object : Callback<TestAudioKitEntity> {
            override fun onResponse(
                call: Call<TestAudioKitEntity>,
                response: Response<TestAudioKitEntity>
            ) {
                response.body()?.let {
                    data(it)
                }

            }

            override fun onFailure(call: Call<TestAudioKitEntity>, t: Throwable) {

            }

        })
    }

    fun signUp(obj: JsonObject): Flow<SignUpEntity?> {
        return flow {
            val res = api.signUp(obj).body()
            emit(res)
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    fun signUpConfirmCode(obj: JsonObject): Flow<ConfirmCodeEntity?> {
        return flow {
            emit(api.signUpConfirmCode(obj).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    fun login(obj: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.login(obj).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    fun loginConfirmCode(obj: JsonObject): Flow<ConfirmCodeEntity?> {
        return flow {
            emit(api.loginConfirmCode(obj).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun createChannel(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.createChannel(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun getMyChannels(body: JsonObject): Flow<MyChannelsEntity?> {
        return flow {
            emit(api.getMyChannels(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun getSpecificChannel(body: JsonObject): Flow<SpecificChannelEntity?> {
        return flow {
            emit(api.getSpecificChannel(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun searchChannel(body: JsonObject): Flow<SearchChannelEntity?> {
        return flow {
            emit(api.searchChannel(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun joinToChannel(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.joinToChannel(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun channelOnlineGames(body: JsonObject): Flow<ChannelGameEntity?> {
        return flow {
            emit(api.onlineGames(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun onlineGamePreStartUpdate(body: JsonObject): Flow<OnlineGameUpdatePreStartEntity?> {
        return flow {
            emit(api.onlineGamePreStartUpdate(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun userHasEnoughGold(body: JsonObject): Flow<UserGoldEntity?> {
        return flow {
            emit(api.userHasEnoughGold(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun getChannelDetail(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.channelDetail(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun userAuth(body: JsonObject): Flow<UserAuthentication?> {
        return flow {
            emit(api.authenticationUser(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun ranking(body: JsonObject): Flow<RankingEntity?> {
        return flow {
            emit(api.getRanking(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun confirmTransaction(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            val res = api.confirmTransaction(body).body()
            Log.i(TAG, "confirmTransaction: $res")
            emit(res)
        }.flowOn(Dispatchers.IO).catch {
            Log.i(TAG, "confirmTransaction: ${it.message}")
            emit(SimpleResponse(status = false, msg = "خطایی رخ داده است", data = {}))
        }
    }

    suspend fun marketItems(body: JsonObject): Flow<MarketEntity?> {
        return flow {
            val result = api.marketItems(body).body()
            Log.i(TAG, "marketItems: $result")
            emit(result)
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun findMatchGold(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.findMatchGold(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun getMyProfile(body: JsonObject): Flow<ProfileEntity?> {
        return flow {
            val a = api.getMyProfile(body).body()
            Log.i(TAG, "getMyProfile: $a")
            emit(a)
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun getLuckyWheelStatus(body: JsonObject): Flow<LuckyWheelStatusEntity?> {
        return flow {
            emit(api.getLuckyWheelStatus(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun spinLuckyWheel(body: JsonObject): Flow<SpinLuckyWheelEntity?> {
        return flow {
            emit(api.spinLuckyWheel(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun userProfile(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.userProfile(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun otherProfile(body: JsonObject):Flow<OtherProfileEntity?>{
        return flow {
            emit(api.othersProfile(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun userTransactionHistory(body: JsonObject): Flow<TransactionEntity?> {
        return flow {
            emit(api.getUserTransactionHistory(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun purchaseItem(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.purchaseItem(body).body())
        }.flowOn(Dispatchers.IO).catch {
            emit(SimpleResponse(status = false, msg = "خطا در خرید", data = {}))
        }
    }


    suspend fun checkUsername(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.checkUsername(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun changeUsername(body: JsonObject): Flow<SimpleResponse?> {
        return flow {
            emit(api.changeUsername(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun userItems(body: JsonObject):Flow<UserItemsEntity?>{
        return flow {
            emit(api.userItems(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun editProfileItem(body: JsonObject):Flow<SimpleResponse?>{
        return flow {
            emit(api.editProfileItem(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }

    suspend fun totalGames(body: JsonObject):Flow<TotalGameHistoryEntity?>{
        return flow {
            emit(api.totalGames(body).body())
        }.flowOn(Dispatchers.IO).catch { emit(null) }
    }
}