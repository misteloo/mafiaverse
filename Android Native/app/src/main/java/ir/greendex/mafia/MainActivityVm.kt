package ir.greendex.mafia

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.entity.PurchaseInfo
import ir.greendex.mafia.entity.main_activity.AppDetailEntity
import ir.greendex.mafia.entity.main_activity.UserJoinDetailEntity
import ir.greendex.mafia.entity.main_activity.UserSocketGoldEntity
import ir.greendex.mafia.repository.local.GetLocalRepositoryEnum
import ir.greendex.mafia.repository.local.LocalRepository
import ir.greendex.mafia.repository.server.ServerRepository
import ir.greendex.mafia.ui.market.MarketListener
import ir.greendex.mafia.util.FIREBASE_TOKEN
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.USER_ID
import ir.greendex.mafia.util.USER_TOKEN
import ir.greendex.mafia.util.payment.BazzarPayment
import ir.greendex.mafia.util.payment.listeners.BazzarPaymentListener
import ir.greendex.mafia.util.payment.listeners.BazzarPurchaseRequestListener
import ir.greendex.mafia.util.socket.SocketInitiator
import ir.greendex.mafia.util.socket.SocketManager
import ir.greendex.mafia.util.voice.KitInitiator
import ir.greendex.mafia.util.voice.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityVm @Inject constructor(
    private val localRepository: LocalRepository,
    private val socketInitiator: SocketInitiator,
    private val gson: Gson,
    private val voiceManager: VoiceManager,
    private val serverRepository: ServerRepository,
    val payment: Payment
) : ViewModel(), BazzarPaymentListener, BazzarPurchaseRequestListener {

    init {
        // socket manager
        SocketManager.setSocket(socket = socketInitiator.getSocket)
        SocketManager.setGson(gson = gson)
        SocketManager.connect()

        // user id and user auth
        SocketManager.userDetail {
            val res = gson.fromJson(it, UserJoinDetailEntity::class.java)
            // bind data to public static
            MainActivity.userId = res.data.userId
            MainActivity.userAuth = res.data.auth
            MainActivity.userJoinedToSocket?.let {
                it(res.data.userId,res.data.auth)
            }
            // store in local
            setUserId(res.data.userId)
        }

        // user gold
        SocketManager.userGold {
            val res = gson.fromJson(it, UserSocketGoldEntity::class.java)
            MainActivity.userGoldCount?.let {
                it(res.data.goldCount)
            }
        }

        // app detail
        SocketManager.appDetail {
            val res = gson.fromJson(it,AppDetailEntity::class.java)
            MainActivity.appDetail?.let {
                it(res.data.version,res.data.serverUpdate)
            }
        }
    }
    private fun setUserId(userId: String) = viewModelScope.launch {
        localRepository.storeData(USER_ID, userId)
    }

    private lateinit var marketListener: MarketListener
    fun initPayment(
        activityResultRegistry: ActivityResultRegistry,
        marketListener: MarketListener
    ) {
        BazzarPayment.setPayment(payment = payment)
        BazzarPayment.setPaymentListeners(paymentListener = this, purchaseRequestListener = this)
        BazzarPayment.setActivityResultRegistry(activityResultRegistry = activityResultRegistry)
        this.marketListener = marketListener
    }

    fun startConstants() = viewModelScope.launch(Dispatchers.IO) {
        SocketManager.setNatoScenarioSocket(natoScenarioSocketManager = socketInitiator.getNatoScenarioSocket)
        SocketManager.setFindMatchSocket(findMatchSocketManager = socketInitiator.getFindMatchSocket)
        SocketManager.setChannelSocket(channelSocket = socketInitiator.getChannelSocket)
        SocketManager.setEndGameSocket(endGameSocketManager = socketInitiator.getEndGameSocket)
        SocketManager.setLocalGameSocket(localGameSocket = socketInitiator.getLocalGameSocket)

        // initialize firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            storeFirebaseToken(token = it.result)
        }

        // kit
        KitInitiator.setVoiceManager(voiceManager = voiceManager)

    }

    fun checkSocketConnection(callback: (Boolean) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        while (true) {
            delay(5000)
            callback(SocketManager.connected())
        }

    }

    suspend fun removeItemFromLocal(keys: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        keys.forEach {
            localRepository.remove(it)
        }
    }


    private fun storeFirebaseToken(token: String) = viewModelScope.launch {
        localRepository.storeData(FIREBASE_TOKEN, token)
    }

    suspend fun getPageRouter() = withContext(Dispatchers.IO) {
        val router =
            async { localRepository.getStoreData(ROUTER, GetLocalRepositoryEnum.String) as String }
        return@withContext router.await()
    }


    fun clearSocketConnections() = viewModelScope.launch(Dispatchers.IO) {
        // clear socket connections
        SocketManager.clearNatoGameSocket()
        SocketManager.clearLocalGameSocketArray()
        SocketManager.clearChannelSocketArray()
        SocketManager.clearEndGameResultSocketArray()
        SocketManager.clearChannelGameSocketArray()
    }

    val getUserIdLiveData = MutableLiveData<String>()
    fun getUserId() = viewModelScope.launch {
        getUserIdLiveData.postValue(
            localRepository.getStoreData(
                USER_ID, GetLocalRepositoryEnum.String
            ) as String
        )
    }

    fun getUserToken(userToken: (String) -> Unit) = viewModelScope.launch {
        userToken(
            localRepository.getStoreData(USER_TOKEN, GetLocalRepositoryEnum.String) as String

        )
    }

    private fun confirmUserTransaction(
        userToken: String,
        plan: String,
        trToken: String
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val obj = JsonObject().apply {
                addProperty("token", userToken)
                addProperty("plan", plan)
                addProperty("tr_token", trToken)
            }

            serverRepository.confirmTransaction(body = obj).collect {
                if (it == null) {
                    marketListener.onMessage("عدم ارتباط")
                } else {
                    if (it.status) BazzarPayment.consumeProduct(token = trToken)
                    else marketListener.onMessage(it.msg)
                }
            }
        }

    override fun onFailureBazzar() {
        marketListener.onMessage("خطا در اتصال به کافه بازار")
    }


    override fun onConsumeSucceed() {
        marketListener.onConsumeSucceed()
    }

    override fun onConsumeFailed() {
        marketListener.onMessage("خطا در مصرف توکن کاربر")
    }

    override fun onBazzarPurchaseFailed() {
        marketListener.onMessage("خطا در خرید محصول")
    }

    override fun onBazzarPurchaseSucceed(it: PurchaseInfo) {
        confirmUserTransaction(
            userToken = MainActivity.userToken!!,
            plan = it.productId,
            trToken = it.purchaseToken
        )
    }

    override fun onBazzarPurchaseCanceled() {
        marketListener.onMessage("خرید شما لغو شد")
    }

    override fun onBazzarPurchaseError() {
        marketListener.onMessage("خطا در فرایند خرید")
    }


    fun dcSocket() {
        SocketManager.disconnectSocket()
    }

    fun dcPayment() {
        BazzarPayment.disconnect()
    }

}