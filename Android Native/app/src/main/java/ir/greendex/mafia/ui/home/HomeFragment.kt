package ir.greendex.mafia.ui.home

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.BuildConfig
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentHomeBinding
import ir.greendex.mafia.entity.game.general.enum_cls.ScenariosEnum
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.game.general.FindMatchBsFragment
import ir.greendex.mafia.game.general.SelectCharacterFragmentDirections
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.MatchType
import ir.greendex.mafia.util.WEBSITE_URL
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.dialog.DialogManager
import ir.greendex.mafia.util.extension.bindToClosingSheet
import ir.greendex.mafia.util.extension.hideAnim
import ir.greendex.mafia.util.extension.showAnim
import ir.greendex.mafia.util.socket.SocketManager
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val vm: HomeVm by viewModels()
    private var serverUpdating: Boolean? = null
    private val alertDialog by lazy { DialogManager(context, soundManager) }

    companion object {
        private const val NOTIFICATION_PERMISSION = 100
        private const val MIC_PERMISSION = 200
    }

    // injection

    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // store routing
        storeRouting()

    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.HOME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get user basic details
        getUserDetails()

        // initViews
        initViews()

        // get notification permission
        getNotificationPermission()

        // home anim
        getHomeFullBodyAnim()

        // basically request
        basicallyRequestFromSocket()

    }

    private fun basicallyRequestFromSocket() {
        MainActivity.userToken?.let { token ->
            SocketManager.requestUserGold(token)
            SocketManager.requestAppDetail()
        }
    }

    private fun getHomeFullBodyAnim() {
        vm.getHomeBodyAnim()
    }

    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION
                )
            }
        }
    }

    private fun getMicPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), MIC_PERMISSION)
            } else startFindMatch()
        } else startFindMatch()
    }

    private fun getUserDetails() {
        lifecycleScope.launch {
            // server update and client
            MainActivity.appDetailCallback { appVersion, serverUpdating ->
                this@HomeFragment.serverUpdating = serverUpdating
                checkVersion(version = appVersion)
                announceUpdating(update = serverUpdating)
            }

            // gold count callback from socket
            MainActivity.userGoldCountCallback { goldCount ->
                lifecycleScope.launch {
                    binding?.apply {
                        val job = lifecycleScope.launch {
                            // gold
                            parent.showAnim(cardGoldParent, direction = Gravity.TOP)
                            // animate gold count
                            ValueAnimator().also {
                                it.setIntValues(0, goldCount)
                                it.duration = 1500
                                it.addUpdateListener { _ ->
                                    txtGoldCount.text = it.animatedValue.toString()
                                }
                            }.start()
                        }
                        job.join()
                        // socket connections
                        fabFindMatch.isEnabled = goldCount > -1
                        if (goldCount > -1) txtNoConnection.visibility =
                            View.INVISIBLE else txtNoConnection.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun announceUpdating(update: Boolean) = lifecycleScope.launch {
        binding?.apply {
            if (update) parent.showAnim(child = cardAnnounceUpdating)
            else parent.hideAnim(child = cardAnnounceUpdating)
        }
    }

    private fun checkVersion(version: String) {
        if (BuildConfig.VERSION_NAME != version) {
            alertDialog.downloadNewAppVersion {
                try {
                    val uri = Uri.parse(WEBSITE_URL)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                } catch (_: Exception) {
                    msg("خطا در بازکردن وب سایت")
                }
            }
        }
    }

    private fun initViews() {
        binding?.apply {
            // find match
            fabFindMatch.setOnClickListener {
                getMicPermission()
            }

            // lucky
            cardLuckyWheel.setOnClickListener {
                hideSmoothBar()
                findNavController().navigate(R.id.action_global_luckyWheelFragment)
            }
            txtLucky.setOnClickListener {
                hideSmoothBar()
                findNavController().navigate(R.id.action_global_luckyWheelFragment)
            }
            // local
            cardLocallyPlay.setOnClickListener {
                hideSmoothBar()
                findNavController().navigate(R.id.action_global_localGameFragment)
            }

            txtLocal.setOnClickListener {
                hideSmoothBar()
                findNavController().navigate(R.id.action_global_localGameFragment)
            }
            // learn
            cardLearn.setOnClickListener {
                hideSmoothBar()
                findNavController().navigate(R.id.action_homeFragment_to_learnFragment)
            }
            txtLearn.setOnClickListener {
                hideSmoothBar()
                findNavController().navigate(R.id.action_homeFragment_to_learnFragment)
            }


            // expand menu
            imgMenu.setOnClickListener {
                menuAnimation()
            }

            // home full body anim
            vm.getHomeBodyAnimLiveData.observe(viewLifecycleOwner) {
                if (it == "not_found") bg.setAnimation(R.raw.default_full_body_anim)
                else bg.setAnimationFromUrl(BASE_URL.plus(it))
                bg.playAnimation()
                bg.loop(true)
            }
        }
    }

    private fun menuAnimation() = lifecycleScope.launch {
        binding?.apply {
            if (menuLinearExpanded.visibility == View.GONE) {

                val job = async {
                    TransitionManager.beginDelayedTransition(cardRootMenu, AutoTransition())
                    menuLinearExpanded.visibility = View.VISIBLE
                }
                job.join()
                imgMenu.setImageResource(R.drawable.round_close_24)
                imgMenu.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

            } else {

                val job = async {
                    TransitionManager.beginDelayedTransition(cardRootMenu, AutoTransition())
                    menuLinearExpanded.visibility = View.GONE
                }
                job.join()
                imgMenu.setImageResource(R.drawable.round_menu_24)
            }
        }
    }

    private fun startFindMatch() = lifecycleScope.launch {
        binding?.apply {
            parent.hideAnim(child = fabFindMatch)
            delay(500)
            parent.showAnim(child = fabFindMatch)
        }
        vm.userHasEnoughGoldToFindMatch {
            if (!it) {
                msg("موجودی سکه حساب شما کافی نیست")
            } else {
                // check server updating
                if (serverUpdating == null) {
                    msg("پاسخی از سمت سرور دریافت نشد")
                } else {
                    if (serverUpdating == true) {
                        msg("در زمان آپدیت جستجوی بازی مجاز نیست")
                    } else {
                        // start finding
                        startFindMatch(
                            matchType = MatchType.RANKED,
                            scenariosEnum = ScenariosEnum.NATO
                        )
                    }
                }
            }
        }
    }

    private fun startFindMatch(matchType: MatchType, scenariosEnum: ScenariosEnum) {
        val bs = FindMatchBsFragment(matchType, scenariosEnum, fromChannel = false)
        // bind to active bottom sheet
        MainActivity.activeBottomSheet = bs
        bs.isCancelable = false
        bs.bindToClosingSheet()
        bs.show(childFragmentManager, null)

        // on game going to start
        bs.gameFoundCallback { scenario, _, gameId ->
            lifecycleScope.launch {
                // store selected scenario
                vm.setFindingMatchScenario(scenario)
                hideSmoothBar()
                delay(200)
                // if you're as moderator ?
                /*if (isCreator) {
                    val action = NatoFragmentDirections.actionToGameNatoFragment(
                        character = null,
                        userId = MainActivity.userId!!,
                        joinType = "moderator",
                        roomId = null,
                        usersData = null,
                        fromReconnect = false,
                        hasGameEvent = false,
                        gameId = gameId
                    )
                    findNavController().navigate(action)
                } else */
                val action =
                    SelectCharacterFragmentDirections.actionGlobalSelectCharacterFragment(gameId = gameId)
                findNavController().navigate(action)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATED")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {

            MIC_PERMISSION -> {
                if (permissions[0] == Manifest.permission.RECORD_AUDIO &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    startFindMatch()
                } else {
                    showSnack(msg = "دسترسی میکروفون داده نشد")
                }
            }
        }
    }

    override fun onResume() {
        showSmoothBar()
        super.onResume()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}