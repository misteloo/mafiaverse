package ir.greendex.mafia.game.nato

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.entity.game.general.ChaosTurnToShakeEntity
import ir.greendex.mafia.entity.game.general.ChaosUserSpeechEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteResultEntity
import ir.greendex.mafia.entity.game.general.CurrentSpeechEntity
import ir.greendex.mafia.entity.game.general.DayInquiryEntity
import ir.greendex.mafia.entity.game.general.DetectiveInquiryEntity
import ir.greendex.mafia.entity.game.general.EndGameResultEntity
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.GodFatherShotEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorStatus
import ir.greendex.mafia.entity.game.general.InGameTurnSpeechEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.general.ModeratorLogEntity
import ir.greendex.mafia.entity.game.general.ReportEntity
import ir.greendex.mafia.entity.game.general.RequestSpeechOptionsEntity
import ir.greendex.mafia.entity.game.general.SpeechEndEntity
import ir.greendex.mafia.entity.game.general.SpeechOptionMsgEntity
import ir.greendex.mafia.entity.game.general.UsersChallengeStatusEntity
import ir.greendex.mafia.entity.game.general.UsersCharacterEntity
import ir.greendex.mafia.entity.game.general.UsingSpeechOptionsEntity
import ir.greendex.mafia.entity.game.general.VoteEntity
import ir.greendex.mafia.entity.game.general.WhichUserRequestSpeechOptionEntity
import ir.greendex.mafia.entity.game.general.enum_cls.AnimActionEnum
import ir.greendex.mafia.entity.game.general.enum_cls.AnimMsgEnum
import ir.greendex.mafia.entity.game.general.enum_cls.NatoGameEventEnum
import ir.greendex.mafia.entity.game.general.enum_cls.UserJoinTypeEnum
import ir.greendex.mafia.entity.game.nato.DayUsedGunEntity
import ir.greendex.mafia.entity.game.nato.GunStatusEntity
import ir.greendex.mafia.entity.game.nato.MafiaDecisionEntity
import ir.greendex.mafia.entity.game.nato.MafiaSpeechEntity
import ir.greendex.mafia.entity.game.nato.NatoCharacters
import ir.greendex.mafia.entity.game.nato.NatoInGameNightUsers
import ir.greendex.mafia.entity.game.nato.NatoMafiaVisitationEntity
import ir.greendex.mafia.entity.game.nato.NatoUseAbilityEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.game.adapter.nato.ModeratorLogAdapter
import ir.greendex.mafia.game.general.InGameReportBsFragment
import ir.greendex.mafia.game.general.MafiaVisitationBsFragment
import ir.greendex.mafia.game.general.action_anim.InGameAnimHandler
import ir.greendex.mafia.game.nato.event.NatoChaos
import ir.greendex.mafia.game.nato.event.NatoDay
import ir.greendex.mafia.game.nato.event.NatoNight
import ir.greendex.mafia.game.nato.event.UserLayer
import ir.greendex.mafia.game.nato.listeners.NatoChaosListener
import ir.greendex.mafia.game.nato.listeners.NatoDayActionListener
import ir.greendex.mafia.game.nato.listeners.NatoNightListener
import ir.greendex.mafia.game.nato.listeners.NatoUserLayerListener
import ir.greendex.mafia.game.nato.listeners.NatoViewModelListener
import ir.greendex.mafia.game.vm.nato.NatoVm
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.dialog.DialogManager
import ir.greendex.mafia.util.extension.bindToClosingDialog
import ir.greendex.mafia.util.extension.bindToClosingSheet
import ir.greendex.mafia.util.extension.hideAnim
import ir.greendex.mafia.util.extension.showAnim
import ir.greendex.mafia.util.socket.SocketManager
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NatoFragment : BaseFragment(), NatoViewModelListener, NatoDayActionListener,
    NatoNightListener, NatoChaosListener, NatoUserLayerListener {

    private var _binding: FragmentNatoBinding? = null
    private val binding get() = _binding
    private val vm: NatoVm by viewModels()
    private val navArgs by navArgs<NatoFragmentArgs>()
    private var isChaosStarted = false
    private var chaosAllSpeech = false
    private var fromReconnect = false
    private var moderatorForceKick = false
    private var usingDayGun = false
    private var dayGun = false
    private val speechQueue by lazy { mutableListOf<InGameTurnSpeechEntity.InGameTurnSpeechQueueData>() }
    private val moderatorLogArray by lazy { mutableListOf<ModeratorLogEntity.ModeratorLogData>() }
    private val dialogManager by lazy { DialogManager(context, soundManager) }
    private lateinit var inGameAnimHandler: InGameAnimHandler
    private lateinit var character: NatoCharacters
    private lateinit var nightIdleBottomSheet: BottomSheetDialogFragment
    private lateinit var dialogWaitingToJoin: AlertDialog
    private lateinit var userJoinType: UserJoinTypeEnum
    private var roomToken: String = ""

    companion object {
        var inGameUsers = mutableListOf<InGameUsersDataEntity.InGameUserData>()
        var userActionHistory = mutableListOf<GameActionEntity.GameActionData>()
        var playerRoles = mutableListOf<UsersCharacterEntity.UsersCharacterData>()
        var myUserId = MainActivity.userId!!
        var moderatorData: InGameUsersDataEntity.InGameUserData? = null
        lateinit var mainGameEvent: NatoGameEventEnum
        lateinit var currentGameEvent: NatoGameEventEnum
    }

    // injection
    @Inject
    lateinit var userLayer: UserLayer

    @Inject
    lateinit var dayUi: NatoDay

    @Inject
    lateinit var nightUi: NatoNight

    @Inject
    lateinit var chaosUi: NatoChaos

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var moderatorLogAdapter: ModeratorLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set socket
        setSocket()
        // set router
        setRouter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNatoBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // getArgument
        getArgument()

        // initView
        initViews()

        // game event from reconnect
        reconnectData()

        // participant speaking
        participantSpeaking()

        // setStartGame
        startGame()

        // initNato
        initNato()


    }


    private fun setRouter() {
        vm.setRouter(router = Routing.GAME)
    }

    private fun moderatorOnUserOnClicked() {
        if (userJoinType != UserJoinTypeEnum.MODERATOR) return
        userLayer.onModeratorClickedToUserCallback {
            // moderator did not active force kick
            if (!moderatorForceKick) return@onModeratorClickedToUserCallback
            // find user
            inGameUsers.find { find ->
                find.userId == it
            }?.let { user ->
                // permission dialog
                dialogManager.moderatorForceKickPermission(user = user) { callback ->
                    if (callback) {
                        // send to server to force kick this player
                        vm.moderatorForceKick(userId = it)
                        soundManager.shotSound()
                        // update moderator force kick button
                        moderatorForceKick = false
                        updateModeratorForceKickButton(state = true, showSnack = false)
                    }
                }
            }
        }
    }

    private fun updateModeratorForceKickButton(state: Boolean, showSnack: Boolean = true) {
        binding?.apply {
            if (state) {
                if (showSnack) showSnackMsg(msg = "شات گرداننده غیر فعال شد")
                layerAnimModerator.also {
                    it.fabForceKick.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                    it.fabForceKick.text = "فعال کردن شات"
                }
            } else {
                if (showSnack) showSnackMsg(msg = "شات گرداننده فعال شد ، بازیکن را انتخاب کنید")
                layerAnimModerator.also {
                    it.fabForceKick.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green800
                        )
                    )
                    it.fabForceKick.text = "غیر فعال کردن"
                }
            }
        }
    }

    private fun reconnectData() = lifecycleScope.launch(Dispatchers.IO) {
        if (navArgs.usersData != null) {
            // set users
            userLayer.setUsers(joinTypeEnum = userJoinType, users = inGameUsers) {
                if (navArgs.hasGameEvent) {
                    lifecycleScope.launch {
                        updateGameEvent(event = currentGameEvent)
                    }
                }
            }
        }
    }

    private fun setSocket() {
        vm.setNatoGameSocket(natoGameSocket = SocketManager.getNatoGameSocket())
    }


    private fun showSnackMsg(msg: String) {
        binding?.apply {
            val snack = Snackbar.make(parent, msg, Snackbar.LENGTH_LONG).apply {
                setTextColor(Color.WHITE)
                anchorView = layerAnimEvent
            }
            ViewCompat.setLayoutDirection(snack.view, ViewCompat.LAYOUT_DIRECTION_RTL)
            snack.show()

        }
    }

    private fun participantSpeaking() {
        vm.onParticipantSpeaking()
        vm.onParticipantSpeakingLiveData.observe(viewLifecycleOwner) {
            if (moderatorData != null) {
                if (moderatorData!!.userId == myUserId) {
                    vm.setModeratorSpeaking(userId = myUserId, speaking = it)
                } else {
                    if (chaosAllSpeech) {
                        vm.setChaosUserSpeech(userId = myUserId, talking = it)
                    }
                }
            } else {
                if (chaosAllSpeech) {
                    vm.setChaosUserSpeech(userId = myUserId, talking = it)
                }
            }
        }
    }


    private fun getArgument() {
        // character
        setCharacter(navArgs.character)
        // type
        userJoinType = when (navArgs.joinType) {
            "player" -> UserJoinTypeEnum.PLAYER
            "observer" -> UserJoinTypeEnum.OBSERVER
            "moderator" -> UserJoinTypeEnum.MODERATOR
            else -> UserJoinTypeEnum.PLAYER
        }

        // from reconnect
        fromReconnect = navArgs.fromReconnect
        if (navArgs.roomId != null) {
            vm.setRoomId(token = navArgs.roomId!!)
        }
    }

    private fun initViews() {
        // show waiting dialog
        waitingToOtherJoinDialog()

        binding?.let {
            // init anim handler
            inGameAnimHandler = InGameAnimHandler(binding = it)
            // user layer
            userLayer.binding(binding = it, userLayerListener = this, userActionListener = this)
            // init day cls
            dayUi.initBinding(
                binding = it,
                fragmentManager = childFragmentManager,
                inGameAnimHandler = inGameAnimHandler
            )
            // night
            nightUi.initBinding(binding = it, fragmentManager = childFragmentManager)
            nightUi.initNightActionLayer(nightActionBinding = it.layerAnimNightAction)
            // chaos
            chaosUi.initBinding(binding = it)
            // set listeners
            dayUi.setListener(actionListener = this)
            nightUi.setListener(listener = this)
            chaosUi.setListener(listener = this)
            // action anim handler
            inGameAnimHandler.init()

            // day gun
            it.cardGun.setOnClickListener { _ ->
                if (dayGun) {
                    dayGun = false
                    usingDayGun = true
                    it.cardGun.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey500
                        )
                    )
                    // anim msg
                    inGameAnimHandler.showMsgAnim(
                        msg = "اسلحه شما فعال شد ، بازیکنت رو انتخاب کن",
                        type = AnimMsgEnum.SIMPLE
                    )

                    // enable using gun
                    userLayer.userActiveDayGun()

                    // run timer
                    userLayer.runGunTimer()

                    return@setOnClickListener
                }
                msg("اسلحه برای شما فعال نیست")
            }

        }

        // binding
        binding?.apply {
            // mic
            vm.micStatusLiveData.observe(viewLifecycleOwner) {
                micToggleUi(status = it)
            }

            // mic toggle
            layerAnimSpeech.fabMicStatus.setOnClickListener {
                vm.setMicStatus(!vm.getMicStatus())
            }

            // mod mic toggle
            if (userJoinType == UserJoinTypeEnum.MODERATOR) {
                layerAnimModerator.fabModeratorMicStatus.setOnClickListener {
                    vm.setMicStatus(!vm.getMicStatus())
                }
            }


            // moderator force kick
            layerAnimModerator.fabForceKick.setOnClickListener {
                // update moderator force kick button
                updateModeratorForceKickButton(state = moderatorForceKick)
                moderatorForceKick = !moderatorForceKick

            }
            // rv
            // moderator event log adapter
            layerModerator.rv.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layerModerator.rv.adapter = moderatorLogAdapter

            // moderator force kick button decoration
            when (userJoinType) {
                UserJoinTypeEnum.MODERATOR -> {
                    layerAnimModerator.fabForceKick.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                }

                else -> {}
            }

            // menu
            imgMenu.setOnClickListener {
                menuAnimation()
            }
        }
    }

    private fun menuAnimation() = lifecycleScope.launch {
        binding?.apply {
            if (cardRootMenu.visibility == View.GONE) {
                val job = async { parent.showAnim(child = cardRootMenu, direction = Gravity.END) }
                job.join()
                imgMenu.setImageResource(R.drawable.round_close_24)
                imgMenu.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                val job = async { parent.hideAnim(child = cardRootMenu, direction = Gravity.END) }
                job.join()
                imgMenu.setImageResource(R.drawable.round_menu_24)
            }
        }
    }

    private fun micToggleUi(status: Boolean) {
        binding?.apply {
            if (userJoinType == UserJoinTypeEnum.MODERATOR) {
                if (status) {
                    layerAnimModerator.also { layer ->
                        layer.fabModeratorMicStatus.icon =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.round_mic_off_24
                            )
                        layer.fabModeratorMicStatus.text = "خاموش کردن"
                    }
                } else {
                    layerAnimModerator.also { layer ->
                        layer.fabModeratorMicStatus.icon =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.round_mic_24
                            )
                        layer.fabModeratorMicStatus.text = "روشن کردن"
                    }
                }
            } else {
                if (status) {
                    layerAnimSpeech.also { layer ->
                        layer.fabMicStatus.icon =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.round_mic_off_24
                            )
                        layer.fabMicStatus.text = "خاموش کردن"
                    }
                } else {
                    layerAnimSpeech.also { layer ->
                        layer.fabMicStatus.icon =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.round_mic_24
                            )
                        layer.fabMicStatus.text = "روشن کردن"
                    }
                }
            }
        }
    }

    private fun waitingToOtherJoinDialog() {
        if (!fromReconnect) {
            if (userJoinType == UserJoinTypeEnum.PLAYER || userJoinType == UserJoinTypeEnum.MODERATOR) {
                dialogWaitingToJoin =
                    dialogManager.waitingToJoin()
                // bind to active dialog
                dialogWaitingToJoin.bindToClosingDialog()
                dialogWaitingToJoin.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogWaitingToJoin.show()
            }
        }
    }

    private fun showNightIdleBs() {
        nightIdleBottomSheet = NatoNightIdleFragment()
        nightIdleBottomSheet.isCancelable = false
        nightIdleBottomSheet.show(childFragmentManager, null)
        // bind to active bottom sheet
        nightIdleBottomSheet.bindToClosingSheet()
    }

    private fun showSpeechQueue() = lifecycleScope.launch(Dispatchers.IO) {
        val users = mutableListOf<InGameUsersDataEntity.InGameUserData>()
        val job = lifecycleScope.launch {
            speechQueue.forEach {
                for (i in inGameUsers.indices) {
                    val user = inGameUsers[i]
                    if (user.userId == it.userId) {
                        users.add(element = user)
                        break
                    }
                }
            }
        }
        job.join()
        // show bs speech queue
        withContext(Dispatchers.Main) {
            val bs = NatoPlayerSpeechQueueBsFragment(details = speechQueue, users = users)
            bs.show(childFragmentManager, null)
        }
    }

    private fun setCharacter(it: String?) = binding?.apply {
        if (it != null) {
            vm.getCharacter(it) { characterEnum, _ ->
                if (characterEnum != null) {
                    character = characterEnum
                    when (character.name) {
                        "CITIZEN" -> {
                            imgCharacter.load(R.drawable.citizen)
                            txtCharacter.text = "شهروند"
                        }

                        "DOCTOR" -> {
                            imgCharacter.load(R.drawable.doctor)
                            txtCharacter.text = "دکتر"
                        }

                        "DETECTIVE" -> {
                            imgCharacter.load(R.drawable.detective)
                            txtCharacter.text = "کاراگاه"
                        }

                        "RIFLEMAN" -> {
                            imgCharacter.load(R.drawable.rifileman)
                            txtCharacter.text = "تفنگدار"
                        }

                        "COMMANDO" -> {
                            imgCharacter.load(R.drawable.commando)
                            txtCharacter.text = "تکاور"
                        }

                        "GUARD" -> {
                            imgCharacter.load(R.drawable.guard)
                            txtCharacter.text = "نگهبان"
                        }

                        "HOSTAGE_TAKER" -> {
                            imgCharacter.load(R.drawable.hostage_taker)
                            txtCharacter.text = "گروگانگیر"
                        }

                        "NATO" -> {
                            imgCharacter.load(R.drawable.nato)
                            txtCharacter.text = "ناتو"
                        }

                        "GODFATHER" -> {
                            imgCharacter.load(R.drawable.godfather)
                            txtCharacter.text = "پدرخوانده"
                        }
                    }
                }
            }
        }
    }

    private fun initNato() {
        vm.initNatoGameManager(this)
    }

    private fun startGame() = lifecycleScope.launch {
        vm.readyToGame()
        vm.storeGameState(state = true)
    }


    // game ready to start
    override fun onUsersData(users: List<InGameUsersDataEntity.InGameUserData>) {
        inGameUsers.clear()
        inGameUsers.addAll(users)
        lifecycleScope.launch {
            users.asFlow().flowOn(Dispatchers.IO).onStart {
                inGameUsers.clear()
            }.onCompletion {
                // dismiss waiting dialog
                if (::dialogWaitingToJoin.isInitialized) {
                    dialogWaitingToJoin.dismiss()
                }
                userLayer.setUsers(joinTypeEnum = userJoinType, users = inGameUsers) {
                    dayUi.onAssetsClick()
                }
            }.collect {
                inGameUsers.add(it)
            }
        }

    }

    /* =================================================== ROOM ID ======================================================== */
    override fun onRoomId(token: String) {
        vm.setRoomId(token = token)
        roomToken = token
    }


    /* =================================================== GAME EVENT ======================================================== */
    override fun onGameEvent(event: NatoGameEventEnum) {
        lifecycleScope.launch {
            updateGameEvent(event = event)
        }
    }

    private fun updateGameEvent(event: NatoGameEventEnum) {
        when (event) {
            NatoGameEventEnum.DAY -> {
                // game event
                mainGameEvent = NatoGameEventEnum.DAY
                currentGameEvent = NatoGameEventEnum.DAY


                // close night idle
                if (::nightIdleBottomSheet.isInitialized) {
                    nightIdleBottomSheet.dismiss()
                    nightUi.nightStatus(startNight = false)
                    inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)

                    binding?.apply {
                        // clear night
                        with(layoutDayAndVote) {
                            layerMaxCount.visibility = View.GONE
                            txtCount.text = ""
                            timeLeft.progress = 0f
                        }
                    }
                }

                binding?.apply {
                    imgGameEvent.load(R.drawable.round_wb_sunny_24)
                    when (userJoinType) {
                        UserJoinTypeEnum.MODERATOR -> {
                            layerModerator.root.visibility = View.VISIBLE
                            // show moderator panel
                            inGameAnimHandler.showActionAnim(type = AnimActionEnum.MODERATOR)
                        }

                        else -> {}
                    }
                }
            }

            NatoGameEventEnum.ACTION -> {
                currentGameEvent = NatoGameEventEnum.ACTION
                // user as player
                binding?.apply {
                    userActionHistory.find {
                        it.userId == myUserId && it.userStatus.isAlive
                    }.let {
                        if (it != null) {
                            inGameAnimHandler.showActionAnim(
                                type = AnimActionEnum.NORMAL
                            )
                        } else {
                            inGameAnimHandler.showActionAnim(type = AnimActionEnum.DEAD)
                        }
                    }
                }
            }

            NatoGameEventEnum.VOTE -> {
                currentGameEvent = NatoGameEventEnum.VOTE
                userActionHistory.find {
                    it.userId == myUserId
                }?.let {
                    if (it.userStatus.isAlive) {
                        inGameAnimHandler.showActionAnim(
                            type = AnimActionEnum.IDLE
                        )
                    } else {
                        inGameAnimHandler.showActionAnim(
                            type = AnimActionEnum.DEAD
                        )
                    }
                }
            }

            NatoGameEventEnum.TARGET_COVERT_ABOUT -> {
                currentGameEvent = NatoGameEventEnum.TARGET_COVERT_ABOUT
            }

            NatoGameEventEnum.NIGHT -> {

                // game event
                currentGameEvent = NatoGameEventEnum.NIGHT
                mainGameEvent = NatoGameEventEnum.NIGHT

                binding?.apply {
                    imgGameEvent.load(R.drawable.round_mode_night_24)

                    nightUi.nightStatus(startNight = true)

                    // change ui to night
                    if (userJoinType == UserJoinTypeEnum.PLAYER) {
                        // show night idle
                        showNightIdleBs()
                        layoutDayAndVote.root.visibility = View.VISIBLE
                    }

                    if (userJoinType == UserJoinTypeEnum.MODERATOR) {
                        dialogManager.simpleMessage(msg = "شب می شود", timer = 5)
                    }

                }
            }

            NatoGameEventEnum.CHAOS -> {
                isChaosStarted = true
                // game event
                currentGameEvent = NatoGameEventEnum.CHAOS
                mainGameEvent = NatoGameEventEnum.CHAOS
                // close night idle
                if (::nightIdleBottomSheet.isInitialized) nightIdleBottomSheet.dismiss()
                binding?.apply {
                    imgGameEvent.load(R.drawable.baseline_choas_24)
                }

                userActionHistory.filter {
                    it.userStatus.isAlive
                }.let { aliveUsers ->
                    lifecycleScope.launch {
                        binding?.apply {
                            layoutDayAndVote.root.visibility = View.GONE
                            layerChaos.root.visibility = View.VISIBLE
                        }
                        chaosUi.startChaos(data = aliveUsers, isModerator = false)

                        /*// chaos ui
                        // moderator
                        if (userJoinType == UserJoinTypeEnum.MODERATOR) {
                            chaosUi.startChaos(data = aliveUsers, isModerator = true)
                        }
                        // players
                        else {

                        }*/
                    }
                }
            }

            NatoGameEventEnum.END -> {
                currentGameEvent = NatoGameEventEnum.END
                mainGameEvent = NatoGameEventEnum.END
                // close night bs
                if (::nightIdleBottomSheet.isInitialized) {
                    nightIdleBottomSheet.dismiss()
                }
                // remove anim
                inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
                // dc kit
                vm.disconnectRoom()
                // turn off mic
                vm.setMicStatus(status = false)
                // clear data
                inGameUsers.clear()
                userActionHistory.clear()
                playerRoles.clear()
            }
        }
    }

    override fun onGameAction(data: List<GameActionEntity.GameActionData>) {
        lifecycleScope.launch(Dispatchers.IO) {

            val job = async {
                if (userActionHistory.isEmpty()) userActionHistory.addAll(data)
                // update user status
                else {
                    for (i in data.indices) {
                        userActionHistory.find {
                            it.userId == data[i].userId
                        }?.let {
                            userActionHistory[userActionHistory.indexOf(it)] = data[i]
                        }
                    }
                }
            }

            job.await()
            job.join()
            if (mainGameEvent == NatoGameEventEnum.CHAOS) {
                // update chaos ui
                userActionHistory.filter {
                    it.userStatus.isAlive
                }.also {
                    chaosUi.updateUserData(usersData = it)
                }
                return@launch
            }

            // bind user action to ui
            dayUi.updateGameAction(data = data)
            // check users mic animation does not work anymore
            dayUi.clearUsersSpeaking()
        }
    }

    /* =================================================== MODERATOR ======================================================== */
    override fun onUsersCharacters(data: List<UsersCharacterEntity.UsersCharacterData>) {
        lifecycleScope.launch {
            userLayer.setCharacterForModerator(it = data)
        }
    }

    override fun onModeratorData(data: InGameModeratorEntity?) {
        lifecycleScope.launch {
            data?.let {
                moderatorData = data.data
                userLayer.moderatorData(data = data.data!!)
                chaosUi.moderatorData(data = data.data!!)
            }
        }
    }

    override fun onModeratorStatus(data: InGameModeratorStatus.InGameModeratorStatusData) {
        lifecycleScope.launch {
            if (mainGameEvent != NatoGameEventEnum.CHAOS) userLayer.moderatorStatus(status = data)
            else chaosUi.moderatorStatus(status = data)
        }
    }

    override fun onModeratorPanelEvent(data: ModeratorLogEntity.ModeratorLogData) {
        lifecycleScope.launch {
            moderatorLogArray.add(data)
            moderatorLogAdapter.modifierItem(newList = moderatorLogArray)
            binding?.apply {
                layerModerator.rv.smoothScrollToPosition(moderatorLogArray.size - 1)
            }
        }
    }


    /* =================================================== GUN ======================================================== */
    override fun onReportGun() {
        lifecycleScope.launch {
            dialogManager.nightMessage(msg = "تفنگدار به شما اسلحه داده")
        }
    }

    override fun onGunStatus(data: GunStatusEntity.GunStatusData) {
        lifecycleScope.launch {
            dayGun = data.isAvailable

            if (data.isAvailable) {
                binding?.apply {
                    cardGun.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                }
            } else {
                binding?.apply {
                    cardGun.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey500
                        )
                    )
                }
            }
        }
    }

    override fun onDayUsingGun(userId: String) {

        lifecycleScope.launch {
            inGameUsers.find {
                it.userId == userId
            }?.let {
                dialogManager.dayUsingGun(user = it)
            }
        }
    }

    override fun onDayUsedGun(data: DayUsedGunEntity.DayUsedGunData) {
        lifecycleScope.launch {
            val fromUser: Deferred<InGameUsersDataEntity.InGameUserData> = async {
                inGameUsers.find {
                    it.userId == data.fromUser
                }!!
            }
            val toUser: Deferred<InGameUsersDataEntity.InGameUserData> = async {
                inGameUsers.find {
                    it.userId == data.toUser
                }!!
            }

            dialogManager.dayUsedGun(
                toUser = toUser.await(),
                fromUser = fromUser.await(),
                gunKind = data.kind
            )
            // play shot sound
            soundManager.shotSound()
        }
    }

    override fun onReport(data: ReportEntity.ReportData) {
        lifecycleScope.launch {
            val user = inGameUsers.find {
                it.userId == data.userId
            }
            dialogManager.reportMessage(msg = data.msg, timer = data.timer, user = user)
        }
    }

    override fun onDayUsingGun() {
        vm.dayUsingGun(myUserId = myUserId)
    }

    override fun onDayUsingGunTimUp() {
        usingDayGun = false
        dayGun = false
        binding?.apply {
            cardGun.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey500
                )
            )
        }
    }


    /* =================================================== SPEECH ======================================================== */
    override fun onStartSpeech() {
        lifecycleScope.launch {
            userActionHistory.find {
                it.userId == myUserId && it.userStatus.isAlive
            }?.apply {
                if (userJoinType == UserJoinTypeEnum.PLAYER) {
                    // anim msg
                    binding?.apply {
                        // action anim
                        inGameAnimHandler.showActionAnim(type = AnimActionEnum.SPEECH)
                        // msg anim
                        inGameAnimHandler.showMsgAnim(type = AnimMsgEnum.MIC)
                        // enable next player
                        layerAnimSpeech.fabNextPlayer.isEnabled = true
                    }
                    dayUi.setMyTurnSpeech(mySpeechTurn = true)
                }
            }
        }
    }

    override fun onSpeechTimeUp(data: SpeechEndEntity.SpeechEndData) {

        lifecycleScope.launch {
            // as player
            if (userJoinType == UserJoinTypeEnum.PLAYER) {
                binding?.apply {
                    inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
                }
            }
            // speech
            dayUi.setMyTurnSpeech(mySpeechTurn = false)
            dayUi.currentSpeechEnd(userId = data.userId)
            // mic
            vm.setMicStatus(status = false)
        }
    }

    override fun onCurrentSpeech(it: CurrentSpeechEntity) {
        lifecycleScope.launch {
            when (currentGameEvent) {
                NatoGameEventEnum.DAY -> dayUi.getCurrentSpeech(currentUser = it)
                NatoGameEventEnum.ACTION -> {
                    if (isChaosStarted) {
                        // clear previous vote result
                        if (mainGameEvent == NatoGameEventEnum.CHAOS) {
                            chaosUi.hideVoteResult()
                            chaosUi.disableVote(userJoinType = userJoinType)
                        }
                        chaosUi.getCurrentSpeech(currentUser = it)
                    } else dayUi.getCurrentSpeech(currentUser = it)
                }

                NatoGameEventEnum.CHAOS -> {
                    // clear previous vote result
                    if (mainGameEvent == NatoGameEventEnum.CHAOS) {
                        chaosUi.hideVoteResult()
                        chaosUi.disableVote(userJoinType = userJoinType)
                    }
                    chaosUi.getCurrentSpeech(currentUser = it)
                }

                else -> dayUi.getCurrentSpeech(currentUser = it)
            }
        }
    }

    override fun onCurrentSpeechEnd(data: SpeechEndEntity.SpeechEndData) {

        lifecycleScope.launch {
            when (currentGameEvent) {
                NatoGameEventEnum.DAY -> dayUi.currentSpeechEnd(userId = data.userId)
                NatoGameEventEnum.ACTION -> {
                    if (isChaosStarted) chaosUi.currentPlayerSpeechEnd()
                    else dayUi.currentSpeechEnd(userId = data.userId)
                }

                NatoGameEventEnum.CHAOS -> chaosUi.currentPlayerSpeechEnd()
                else -> {}
            }
        }
    }

    override fun onMafiaSpeech(data: MafiaSpeechEntity) {
        // room token
        vm.setRoomId(token = data.token)
        lifecycleScope.launch {
            if (::nightIdleBottomSheet.isInitialized) {
                nightIdleBottomSheet.dismiss()
            }

            inGameAnimHandler.showActionAnim(type = AnimActionEnum.SPEECH)

            binding?.apply {
                layerAnimSpeech.fabNextPlayer.isEnabled = false
            }
            dayUi.getCurrentSpeech(
                currentUser = CurrentSpeechEntity(
                    currentUserId = data.teammate,
                    timer = data.timer,
                    hasNext = false
                )
            )
        }
    }

    override fun onMafiaSpeechEnd() {
        vm.setMicStatus(status = false)
        lifecycleScope.launch {
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
            showNightIdleBs()
            binding?.apply {
                layerAnimSpeech.fabNextPlayer.isEnabled = true
            }

            dayUi.mafiaSpeechEnd()
        }

    }

    override fun onInGameTurnSpeechQueue(it: InGameTurnSpeechEntity) {
        speechQueue.clear()
        speechQueue.addAll(it.data.queue)
    }

    override fun onNextPlayer() {
        // disable mic
        vm.setMicStatus(status = false)
        // change event anim
        binding?.apply {
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
        }

        // next player
        vm.nextSpeech()
    }


    /* =================================================== VOTE ======================================================== */
    override fun onVoteToPlayer(data: VoteEntity.VoteData) {
        lifecycleScope.launch {
            binding?.apply {
                // update vote ui
                data.availableUsers.find {
                    it == myUserId
                }?.apply {
                    userActionHistory.find { history ->
                        history.userId == myUserId && history.userStatus.isAlive
                    }?.apply {
                        inGameAnimHandler.showActionAnim(type = AnimActionEnum.VOTE)
                        object : CountDownTimer(3000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {

                            }

                            override fun onFinish() {
                                inGameAnimHandler.showActionAnim(
                                    type = AnimActionEnum.IDLE
                                )
                            }

                        }.start()
                    }
                }
            }
        }
    }

    override fun onSubmitVote() {

        binding?.apply {
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
        }

        // send vote to server
        vm.voteToPlayer()
    }

    override fun onDayInquiry(data: DayInquiryEntity.DayInquiryData) {
        lifecycleScope.launch {
            dialogManager.simpleMessage(msg = "آیا شهر استعلام میخواهد ؟", timer = data.timer)
        }
    }

    override fun onDayInquiryResult(data: DayInquiryEntity.DayInquiryData) {
        lifecycleScope.launch {
            dialogManager.simpleMessage(msg = data.msg!!, timer = data.timer)
        }
    }


    /* =================================================== TARGET COVER ABOUT ======================================================== */
    override fun onSpeechOptions(speechOption: UsingSpeechOptionsEntity.UsingSpeechOptionsData) {
        lifecycleScope.launch {
            // callback
            dialogManager.usingSpeechOption(msg = speechOption.msg, timer = speechOption.timer) {
                vm.usingSpeechOption(usingOption = it)
            }
        }
    }

    override fun onWhichUserRequestSpeechOption(which: WhichUserRequestSpeechOptionEntity.WhichUserRequestSpeechOptionData) {
        inGameUsers.find {
            it.userId == which.requesterId
        }?.let {
            lifecycleScope.launch {
                dialogManager.whichUserRequestToSpeechOptions(user = it, timer = which.timer)
            }
        }
    }

    override fun onSpeechOptionMsg(msg: SpeechOptionMsgEntity.SpeechOptionMsgData) {
        lifecycleScope.launch {
            dialogManager.speechOptionMsg(timer = msg.timer, msg = msg.msg)
        }
    }

    override fun onRequestSpeechOption(data: RequestSpeechOptionsEntity.RequestSpeechOptionsData) {
        lifecycleScope.launch {
            dayUi.setWhichUserRequestTargetCover(userId = data.requesterId)
            inGameUsers.find {
                it.userId == data.requesterId
            }?.let {
                dialogManager.requestSpeechOption(
                    user = it,
                    timer = data.timer,
                    option = data.option
                ) {
                    userActionHistory.find { find ->
                        find.userId == myUserId && find.userStatus.isAlive
                    }?.apply {
                        if (data.requesterId != myUserId) binding?.apply {
                            inGameAnimHandler.showActionAnim(type = AnimActionEnum.VOTE)
                        }
                    }
                    // timer to hand down
                    object : CountDownTimer(data.timer * 1000L, 1000) {
                        override fun onTick(millisUntilFinished: Long) {

                        }

                        override fun onFinish() {
                            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
                        }

                    }.start()
                }
            }
        }
    }

    override fun onSubmitHandRiseToTargetCover(userId: String) {
        vm.selectVolunteerToTargetCover(userId = userId)
    }

    /* =================================================== CHALLENGE ======================================================== */

    override fun onChallengeAccepted() {
        lifecycleScope.launch {
            binding?.apply {
                //set text
                txtMsg.text = "چالش شما تایید شد"
                layerAnimMsg.transitionToState(R.id.simpleMsg)
            }
        }
    }

    override fun onChallengeReq() {
        vm.setGameAction(challenge = true)
    }

    // send specific user challenge accepted to server
    override fun onChallengeAccepted(userId: String) {
        vm.acceptChallenge(userId = userId)
    }

    override fun onUsersChallengeStatus(userChallengeStatus: List<UsersChallengeStatusEntity.UserChallengeStatusData>) {
        lifecycleScope.launch {
            dayUi.challengeStatus(challengeList = userChallengeStatus)
        }
    }

    /* =================================================== LIKE / DISLIKE ======================================================== */
    override fun onLike() {
        vm.setGameAction(like = true)
    }

    override fun onDislike() {
        vm.setGameAction(dislike = true)
    }


    /* =================================================== NIGHT ======================================================== */

    override fun onMafiaVisitation(mafiaList: List<NatoMafiaVisitationEntity>) {
        lifecycleScope.launch {
            val bs = MafiaVisitationBsFragment(mafiaList = mafiaList, users = inGameUsers)
            bs.isCancelable = false
            bs.show(childFragmentManager, null)
            // bind to active bottom sheet
            bs.bindToClosingSheet()
        }
    }

    override fun onUseAbility(it: NatoUseAbilityEntity.NatoUseAbilityData) {
        lifecycleScope.launch {
            // act enable
            if (it.canAct) {
                // dismiss waiting
                if (::nightIdleBottomSheet.isInitialized) {
                    nightIdleBottomSheet.dismiss()
                }

                // show dialog to night act time
                showDialogToNightActTime()
                // ring the bell
                soundManager.bellSound()
                // bind ui
                nightUi.nightAct(
                    users = it.availableUsers,
                    timer = it.timer,
                    maxCount = it.maxCount,
                    currentPlayerCharacter = character,
                    mafiaShot = false
                )
                binding?.apply {
                    // change action to night if player is not riffle man or nato
                    if (character == NatoCharacters.RIFLEMAN || character == NatoCharacters.NATO)
                        inGameAnimHandler.showActionAnim(
                            type = AnimActionEnum.IDLE
                        ) else inGameAnimHandler.showActionAnim(type = AnimActionEnum.NIGHT)
                    // msg anim
                    inGameAnimHandler.showMsgAnim(
                        msg = "از بین بازیکنا هدفت رو انتخاب کن",
                        type = AnimMsgEnum.SIMPLE
                    )
                }
            } else dialogManager.nightMessage(msg = it.msg)

        }
    }

    private fun showDialogToNightActTime(mafiaAct: Boolean = false) {
        if (mafiaAct) {
            dialogManager.simpleMessage(
                msg = "پدرخوانده نوبت شماست",
                resImage = R.drawable.godfather,
                timer = 2
            )
            return
        }
        dialogManager.simpleMessage(
            msg = when (character) {
                NatoCharacters.NATO -> "ناتو نوبت شماست"
                NatoCharacters.HOSTAGE_TAKER -> "گروگانگیر نوبت شماست"
                NatoCharacters.GODFATHER -> "پدرخوانده نوبت شماست"
                NatoCharacters.DETECTIVE -> "کاراگاه نوبت شماست"
                NatoCharacters.RIFLEMAN -> "تفنگدار نوبت شماست"
                NatoCharacters.COMMANDO -> "کماندو نوبت شماست"
                NatoCharacters.DOCTOR -> "دکتر نوبت شماست"
                NatoCharacters.GUARD -> "نگهبان نوبت شماست"
                else -> ""
            }, timer = 2, resImage = when (character) {
                NatoCharacters.NATO -> R.drawable.nato
                NatoCharacters.HOSTAGE_TAKER -> R.drawable.hostage_taker
                NatoCharacters.GODFATHER -> R.drawable.godfather
                NatoCharacters.DETECTIVE -> R.drawable.detective
                NatoCharacters.RIFLEMAN -> R.drawable.rifileman
                NatoCharacters.COMMANDO -> R.drawable.commando
                NatoCharacters.DOCTOR -> R.drawable.doctor
                NatoCharacters.GUARD -> R.drawable.guard
                else -> 0
            }
        )
    }

    override fun onNightActResult(users: List<NatoInGameNightUsers>, mafiaShot: Boolean) {
        vm.sendNightActToServer(
            users = users,
            role = character.name.lowercase(),
            mafiaShot = mafiaShot
        )
        if (mafiaShot) {
            soundManager.shotSound()
        }
        // show bs waiting
        showNightIdleBs()

        binding?.apply {
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
            // max count
            layoutDayAndVote.also {
                it.txtCount.text = ""
                it.layerMaxCount.visibility = View.GONE
            }
        }
    }

    override fun onNightActTimeUp() {
        lifecycleScope.launch {
            binding?.apply {
                inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)

                // max count
                layoutDayAndVote.also {
                    it.txtCount.text = ""
                    it.layerMaxCount.visibility = View.GONE
                }
            }
            // show bs waiting
            showNightIdleBs()
        }
    }

    override fun onMafiaDecision(data: MafiaDecisionEntity) {
        lifecycleScope.launch {
            dialogManager.mafiaDecision(timer = data.timer) {
                if (it != null) {
                    if (it) vm.sendMafiaDecision(shot = true, nato = false, role = character)
                    else vm.sendMafiaDecision(shot = false, nato = true, role = character)
                }
            }
        }
    }

    // mafia shot from server
    override fun onMafiaShot(it: GodFatherShotEntity) {
        lifecycleScope.launch {
            // dismiss waiting
            nightIdleBottomSheet.dismiss()
            // show dialog to night act time
            showDialogToNightActTime(mafiaAct = true)
            // ring the bell
            soundManager.bellSound()
            // bind ui
            nightUi.nightAct(
                users = it.availableUsers,
                timer = it.timer,
                maxCount = it.maxCount,
                currentPlayerCharacter = character,
                mafiaShot = true
            )

            binding?.apply {
                // change action to night
                inGameAnimHandler.showActionAnim(type = AnimActionEnum.NIGHT)
                // msg anim
                inGameAnimHandler.showMsgAnim(
                    msg = "نوبت شماست از بین بازیکنا هدفت رو انتخاب کن",
                    type = AnimMsgEnum.SIMPLE
                )
            }
        }
    }

    override fun onDetectiveInquiry(it: DetectiveInquiryEntity) {
        lifecycleScope.launch {
            inGameUsers.find { data ->
                data.userId == it.userId
            }?.let { find ->
                dialogManager.detectiveInquiry(
                    user = find,
                    inquiry = it.inquiry
                )
            }
        }
    }

    /* =================================================== CHAOS ======================================================== */

    override fun onChaosVoteTimeNotification() {
        lifecycleScope.launch {
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
            dialogManager.simpleMessage(msg = "نوبت رای گیری کی آس میرسه", timer = 3)
            soundManager.bellSound()
        }
    }

    override fun onChaosVoteResult(data: ChaosVoteResultEntity) {
        lifecycleScope.launch {
            chaosUi.voteResult(result = data.data)
        }
    }

    override fun onChaosVote(data: ChaosVoteEntity.ChaosVoteData) {
        lifecycleScope.launch {
            // start vote
            chaosUi.startVote(data = data)
        }
    }

    override fun onChaosUserSpeech(data: List<ChaosUserSpeechEntity.ChaosUserSpeechData>) {
        lifecycleScope.launch {
            chaosUi.allSpeechData(users = data)
        }
    }

    override fun onChaosAllSpeech() {
        lifecycleScope.launch {
            // disable next player
            binding?.layerAnimSpeech?.apply {
                fabNextPlayer.isEnabled = false
            }
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.SPEECH)
            chaosAllSpeech = true
        }
    }


    override fun onChaosAllSpeechEnd() {
        chaosAllSpeech = false
        vm.setMicStatus(status = chaosAllSpeech)
        lifecycleScope.launch {
            chaosUi.endAllSpeech()
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
        }
    }

    override fun onChaosTurnToShake(data: ChaosTurnToShakeEntity.ChaosTurnToShakeData) {
        lifecycleScope.launch {
            chaosUi.onTurnToShake(userId = data.userId)
        }
    }

    override fun onLastDecision(data: ChaosVoteEntity) {
        lifecycleScope.launch {
            // start vote
            chaosUi.startVote(data = data.data)
        }
    }

    override fun onPlayerShowCharacter() {
        lifecycleScope.launch {
            binding?.apply {
                imgCharacter.load(R.drawable.citizen)
                character = NatoCharacters.CITIZEN
                txtCharacter.text = "شهروند"
            }
        }
    }

    override fun onAbandon() {
        lifecycleScope.launch(Dispatchers.IO) {
            // clear data
            userLayer.clearLayerArray()
            inGameUsers.clear()
            userActionHistory.clear()
            playerRoles.clear()
            // clear connections
            vm.turnOffGameSocket()
            // dc room
            vm.disconnectRoom()
            // tell to server user getting out from game
            vm.userEndGame()
            // turn off mic
            vm.setMicStatus(status = false)
            // navigation
            withContext(Dispatchers.Main) {
                val bundle = Bundle().apply {
                    putBoolean("join_request", true)
                }
                findNavController().navigate(R.id.action_gameNatoFragment_to_homeFragment, bundle)
            }
        }
    }

    override fun onActionEnd() {
        lifecycleScope.launch {
            inGameAnimHandler.showActionAnim(type = AnimActionEnum.IDLE)
        }
    }


    override fun onVoteToSpecificUser(userId: String) {
        vm.chaosUserVoteToUser(userId = userId)
    }

    /* =================================================== END GAME ======================================================== */
    override fun onEndGameResult(data: EndGameResultEntity.EndGameResultData) {
        lifecycleScope.launch(Dispatchers.IO) {
            // clear data
            userLayer.clearLayerArray()
            inGameUsers.clear()
            userActionHistory.clear()
            playerRoles.clear()
            // clear connections
            vm.turnOffGameSocket()
            // dc room
            vm.disconnectRoom()
            // tell to server user getting out from game
            vm.userEndGame()
            // turn off mic
            vm.setMicStatus(status = false)
            withContext(Dispatchers.Main) {
                dialogManager.dialogGameResult(gameResult = data, userId = myUserId) {
                    if (it) {
                        val bundle = Bundle().apply {
                            putParcelable("result", data)
                            putString("roomId", roomToken)
                        }
                        findNavController().navigate(R.id.action_global_gameResultFragment, bundle)
                    } else {
                        findNavController().navigate(
                            R.id.action_gameNatoFragment_to_homeFragment
                        )
                    }
                }
            }
        }
    }

    /* =================================================== CLICK ON USER ======================================================== */
    override fun onClickedToUser(userId: String) {

        if (mainGameEvent == NatoGameEventEnum.DAY) {
            // disable self
            if (userId == myUserId) {
                longMsg("کلیک بر روی خودتان مجاز نیست")
                return
            } else if (usingDayGun) {
                userActionHistory.find {
                    it.userId == myUserId && it.userStatus.isAlive
                }.let {
                    // means you're not dead
                    if (it != null) {
                        usingDayGun = false
                        // cancel timer
                        userLayer.cancelGunTimer()
                        // riffle using gun on day
                        vm.rifleGunShot(userId = userId)
                    } else {
                        showSnackMsg("شما مرده اید نمیتوانید استفاده کنید")
                    }
                }
            }

        } else if (mainGameEvent == NatoGameEventEnum.NIGHT) nightUi.getUserIdOnNightActing(userId = userId)
    }

    override fun onLongClickedToUser(userId: String) {
        if (mainGameEvent == NatoGameEventEnum.DAY) {
            userActionHistory.find {
                it.userId == myUserId
            }?.let {
                if (it.userStatus.isAlive) reportPlayer()
                else showSnackMsg("کاری از دست روح ساخته نیست")
            }
        } else {
            showSnackMsg("این گزینه تو روز فعاله")
        }
    }

    private fun reportPlayer() {
        val bs = InGameReportBsFragment(verifiedGame = false)
        bs.show(childFragmentManager, null)

    }

    override fun onResume() {
        super.onResume()
        activity?.onBackPressedDispatcher?.addCallback {
            dialogManager.dialogExitGame {
                lifecycleScope.launch(Dispatchers.IO) {
                    userLayer.clearLayerArray()
                    inGameUsers.clear()
                    userActionHistory.clear()
                    playerRoles.clear()

                    // clear connections
                    vm.turnOffGameSocket()
                    vm.setMicStatus(status = false)
                    vm.storeGameState(state = false)
                    // dc room
                    vm.disconnectRoom()
                    // abandon
                    if (it) SocketManager.abandonWithoutGameId()
                    // navigate home

                    withContext(Dispatchers.Main) {
                        val bundle = Bundle().apply {
                            putBoolean("join_request", true)
                        }
                        findNavController().navigate(
                            R.id.action_gameNatoFragment_to_homeFragment,
                            bundle
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        vm.setMicStatus(status = false)
        super.onDestroy()
    }

}