package ir.greendex.mafia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.adivery.sdk.Adivery
import com.adivery.sdk.AdiveryListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.ActivityMainBinding
import ir.greendex.mafia.databinding.LayerDialogNoInternetBinding
import ir.greendex.mafia.game.general.ReconnectFragmentDirections
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.ui.market.MarketListener
import ir.greendex.mafia.util.ADIVERY_RSA
import ir.greendex.mafia.util.AD_REWARDED_VIDEO
import ir.greendex.mafia.util.USER_ID
import ir.greendex.mafia.util.USER_PHONE
import ir.greendex.mafia.util.USER_TOKEN
import ir.greendex.mafia.util.dialog.DialogManager
import ir.greendex.mafia.util.payment.BazzarPayment
import ir.greendex.mafia.util.socket.SocketManager
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MarketListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogNoInternet: AlertDialog
    private lateinit var dialogNoInternetView: LayerDialogNoInternetBinding
    private val vm: MainActivityVm by viewModels()
    private val dialogManager by lazy { DialogManager(context = this, soundManager = soundManager) }
    private var socketStatusConnection = false


    companion object {
        private var adListener: AdListener? = null
        var userToken: String? = null
        var userId: String? = null
        var userAuth: Boolean = false
        var emitJoinToSocket = true
        lateinit var activeBottomSheet: BottomSheetDialogFragment
        lateinit var activeDialog: AlertDialog
        private val isActiveDialogInitialized get() = ::activeDialog.isInitialized
        private val isActiveBottomSheetInitialized get() = ::activeBottomSheet.isInitialized

        /* socket */

        var userGoldCount: ((Int) -> Unit)? = null
        fun userGoldCountCallback(it: (Int) -> Unit) {
            userGoldCount = it
        }

        var appDetail: ((version: String, serverUpdate: Boolean) -> Unit)? = null
        fun appDetailCallback(it: (String, Boolean) -> Unit) {
            appDetail = it
        }

        var userJoinedToSocket: ((userId: (String), auth: (Boolean)) -> Unit)? = null
        fun userJoinedToSocketCallback(it: (userId: (String), auth: (Boolean)) -> Unit) {
            userJoinedToSocket = it
        }

        private fun joinToSocket() {
            userToken?.let { token ->
                val obj = JSONObject().apply {
                    put("token", token)
                }
                SocketManager.joinUserToServer(obj = obj)
            }
        }

        /* payment */

        fun connectToPayment() {
            if (!BazzarPayment.getPaymentConnection()) {
                BazzarPayment.connectToPayment()
            }
        }

        fun purchaseProduct(
            sku: String, payload: String
        ) {
            BazzarPayment.purchaseRequest(productId = sku, payload = payload)
        }

        private var onUpdateMarketItems: (() -> Unit)? = null
        fun onUpdateMarketItemsCallback(it: () -> Unit) {
            onUpdateMarketItems = it
        }


        // bind ad listener
        fun adListener(adListener: AdListener) {
            this.adListener = adListener
        }

        var rewardedAdAvailable = false

    }

    // injection

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var popupMenu: PopupMenu

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // orientation locked to portrait
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // prevent screen off
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // lock app language
        setAppLanguage()
        // binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // content
        setContentView(binding.root)
        // user token
        getUserToken()
        // init Views
        initViews()

        // init payment
        initPayment()

        // start constants
        startConstants()

        // get user id
        getUserId()

        // prepare dialog no internet
        prepareDialogNoInternet()

        // reconnect to game
        reconnectToGame()

        // socket watcher
        socketConnectionWatcher()

        // tap sell
        initAd()

        // force exit listener
        forceExitListener()

        // join to socket
        MainActivity.joinToSocket()
    }

    private fun getUserToken() {
        vm.getUserToken {
            userToken = if (it == "not_found") null else it
        }
    }

    @Suppress("DEPRECATION")
    private fun setAppLanguage() {
        val languageToLoad = "en" // your language
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config, baseContext.resources.displayMetrics
        )
    }

    private fun reconnectToGame() {
        SocketManager.onReconnectToGameAvailable { callback ->
            lifecycleScope.launch {
                dialogManager.dialogReconnectNotification {
                    // true => reconnect // false => abandon

                    if (it) {
                        // send to server
                        SocketManager.reconnectToGame(gameId = callback.gameId)
                        // action
                        val joinType = if (callback.isPlayer) "player"
                        else if (callback.isSupervisor) "supervisor"
                        else "observer"
                        val action = ReconnectFragmentDirections.actionGlobalReconnectFragment(
                            joinType = joinType,
                            userId = userId!!,
                            gameId = callback.gameId,
                            character = callback.character
                        )
                        // navigate
                        navController.navigate(action)
                        // hide smooth bar
                        binding.apply {
                            mainActivityParent.transitionToStart()
                        }
                    }
                    // abandon
                    else SocketManager.abandonWithGameId(gameId = callback.gameId)
                }
            }
        }
    }

    private fun getUserId() {
        vm.getUserId()
    }

    private fun initPayment() {
        vm.initPayment(activityResultRegistry = activityResultRegistry, marketListener = this)
    }

    private fun forceExitListener() {
        SocketManager.forceExit {
            // remove
            lifecycleScope.launch {
                userToken = null
                // remove
                vm.removeItemFromLocal(listOf(USER_ID, USER_PHONE, USER_TOKEN))
                // clear socket connections
                vm.clearSocketConnections()
                // socket off
                vm.dcSocket()
                binding.apply {
                    mainActivityParent.transitionToStart()
                }
                navController.navigate(R.id.action_global_splashFragment)
                Toast.makeText(
                    this@MainActivity, "نمیتوانید از 2 قسمت وارد برنامه شوید", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initAd() {
        // ad
        Adivery.configure(application, ADIVERY_RSA)
        Adivery.addGlobalListener(object : AdiveryListener() {
            override fun onRewardedAdLoaded(placementId: String) {
                rewardedAdAvailable = true
                super.onRewardedAdLoaded(placementId)
            }

            override fun onRewardedAdClosed(placementId: String, isRewarded: Boolean) {
                adListener?.onRewardedAdGranted(granted = isRewarded)

            }
        })

        // prepare rewarded ad
        Adivery.prepareRewardedAd(this@MainActivity, AD_REWARDED_VIDEO)

    }

    private fun startConstants() {
        vm.startConstants()
    }

    private fun socketConnectionWatcher() {
        vm.checkSocketConnection {

            if (userToken != null) {
                socketStatusConnection = it
                if (!socketStatusConnection) {
                    emitJoinToSocket = true
                    if (!dialogNoInternet.isShowing) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            // close dialog and bottom sheet while is open
                            if (isActiveDialogInitialized) activeDialog.dismiss()
                            if (isActiveBottomSheetInitialized) activeBottomSheet.dismiss()

                            dialogNoInternet.show()
                        }
                    }

                    // clear socket connections
                    vm.clearSocketConnections()

                    // check router state
                    checkRouterState()

                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        dialogNoInternet.dismiss()

                        // join
                        joinToSocket()

                        // prevent duplication emit
                        emitJoinToSocket = false
                    }
                }
            }
        }
    }

    private fun joinToSocket() {
        if (emitJoinToSocket) {
            MainActivity.joinToSocket()
        }
    }

    private fun checkRouterState() = lifecycleScope.launch {
        when (vm.getPageRouter()) {
            "GAME" -> navController.navigate(R.id.action_global_homeFragment)
            else -> {
                if (vm.getPageRouter() != "HOME") {
                    binding.apply {
                        smoothBar.selectedItemId = R.id.homeFragment
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.apply {
            // nav
            val fragmentContainer =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            navController = fragmentContainer.navController
            // smooth bar
            smoothBar.setupWithNavController(navController)
            // observe user id
            vm.getUserIdLiveData.observe(this@MainActivity) {
                userId = it
            }
        }
    }

    private fun prepareDialogNoInternet() {
        dialogNoInternetView = LayerDialogNoInternetBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this).apply {
            setCancelable(false)
            setView(dialogNoInternetView.root)
        }
        dialogNoInternet = builder.create()
        dialogNoInternet.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }

    override fun onConsumeSucceed() {
        onUpdateMarketItems?.let {
            it()
        }
    }

    override fun onMessage(it: String) {
        lifecycleScope.launch {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        // current router
        /*lifecycleScope.launch(Dispatchers.IO){
            val page = vm.getPageRouter()
            if (page == "GAME"){
                NatoFragment.inGameUsers.clear()
                NatoFragment.userActionHistory.clear()
                NatoFragment.playerRoles.clear()

                // clear connections
                vm.turnOffGameSocket()

                // dc room
                vm.dcRoomAudio()

                // navigate home
                withContext(Dispatchers.Main) {
                    navController.navigate(
                        R.id.action_gameNatoFragment_to_homeFragment,
                    )
                }
            }
        }*/
        super.onResume()
    }
    override fun onDestroy() {
        // socket off
        vm.dcSocket()
        // bazzar payment off
        vm.dcPayment()
        super.onDestroy()
    }


}
