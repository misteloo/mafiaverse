package ir.greendex.mafia.repository

import com.google.gson.JsonObject
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
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("user/test_room")
    fun testKit(@Body body: JsonObject): Call<TestAudioKitEntity>

    @POST("registion/sign_up")
    suspend fun signUp(@Body `object`: JsonObject): Response<SignUpEntity>

    @POST("registion/log_in")
    suspend fun login(@Body obj: JsonObject): Response<SimpleResponse>

    @POST("registion/sign_up_confirm_phone")
    suspend fun signUpConfirmCode(@Body `object`: JsonObject): Response<ConfirmCodeEntity>

    @POST("registion/log_in_confirm_phone")
    suspend fun loginConfirmCode(@Body `object`: JsonObject): Response<ConfirmCodeEntity>

    @POST("channel/create_channel_by_user")
    suspend fun createChannel(@Body body: JsonObject): Response<SimpleResponse>

    @POST("channel/my_channels")
    suspend fun getMyChannels(@Body body: JsonObject): Response<MyChannelsEntity>

    @POST("channel/specific_channel")
    suspend fun getSpecificChannel(@Body body: JsonObject): Response<SpecificChannelEntity>

    @POST("channel/search")
    suspend fun searchChannel(@Body body: JsonObject): Response<SearchChannelEntity>

    @POST("channel/join_request")
    suspend fun joinToChannel(@Body body: JsonObject): Response<SimpleResponse>

    @POST("channel/channel_detail")
    suspend fun channelDetail(@Body body: JsonObject): Response<SimpleResponse>

    @POST("channel/online_game")
    suspend fun onlineGames(@Body body: JsonObject): Response<ChannelGameEntity>

    @POST("channel/online_game_pre_start_update")
    suspend fun onlineGamePreStartUpdate(@Body body: JsonObject): Response<OnlineGameUpdatePreStartEntity>

    @POST("user/has_enough_gold")
    suspend fun userHasEnoughGold(@Body body: JsonObject): Response<UserGoldEntity>

    @POST("user/age_auth")
    suspend fun authenticationUser(@Body body: JsonObject): Response<UserAuthentication>

    @POST("ranking/")
    suspend fun getRanking(@Body body: JsonObject): Response<RankingEntity>

    @POST("transaction/confirm_transaction")
    suspend fun confirmTransaction(@Body body: JsonObject): Response<SimpleResponse>

    @POST("items/items_list")
    suspend fun marketItems(@Body body: JsonObject): Response<MarketEntity>

    @POST("user/find_match_gold")
    suspend fun findMatchGold(@Body body: JsonObject): Response<SimpleResponse>

    @POST("user/profile")
    suspend fun getMyProfile(@Body body: JsonObject): Response<ProfileEntity>

    @POST("user/lucky_wheel_status")
    suspend fun getLuckyWheelStatus(@Body body: JsonObject): Response<LuckyWheelStatusEntity>

    @POST("user/spin_lucky_wheel")
    suspend fun spinLuckyWheel(@Body body: JsonObject): Response<SpinLuckyWheelEntity>

    @POST("user/profile")
    suspend fun userProfile(@Body body: JsonObject):Response<SimpleResponse>

    @POST("user/others_profile")
    suspend fun othersProfile(@Body body: JsonObject):Response<OtherProfileEntity>

    @POST("user/user_transactions")
    suspend fun getUserTransactionHistory(@Body body: JsonObject):Response<TransactionEntity>

    @POST("items/buy")
    suspend fun purchaseItem(@Body body: JsonObject):Response<SimpleResponse>

    @POST("user/check_new_user_name")
    suspend fun checkUsername(@Body body: JsonObject):Response<SimpleResponse>

    @POST("user/change_user_name")
    suspend fun changeUsername(@Body body: JsonObject):Response<SimpleResponse>

    @POST("user/items_list")
    suspend fun userItems(@Body body: JsonObject):Response<UserItemsEntity>

    @POST("user/edit_profile")
    suspend fun editProfileItem(@Body body: JsonObject):Response<SimpleResponse>

    @POST("user/game_history")
    suspend fun totalGames(@Body body: JsonObject):Response<TotalGameHistoryEntity>
}
