package ir.greendex.mafia.ui.group.tap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentGroupGameBinding
import ir.greendex.mafia.entity.channel.ChannelGameData
import ir.greendex.mafia.entity.channel.OnlineGameUpdatePreStartEntity
import ir.greendex.mafia.ui.group.adapter.GroupGameListAdapter
import ir.greendex.mafia.ui.group.adapter.GroupGamePreStartAdapter
import ir.greendex.mafia.ui.group.listeners.ChannelGameVmListener
import ir.greendex.mafia.ui.group.vm.GroupGameVm
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.dialog.DialogManager
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GroupGameFragment(
    private val channelId: String,
    private val token: String,
    private val userId: String,
) : BaseFragment(), ChannelGameVmListener {
    private var _binding: FragmentGroupGameBinding? = null
    private val binding get() = _binding
    private val users by lazy { mutableListOf<OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData>() }
    private val dialogManager by lazy { DialogManager(context, soundManager) }
    private val vm: GroupGameVm by viewModels()
    private var confirmedPage = true
    private var hasCreatedGame = false

    // injection
    @Inject
    lateinit var inGameUsersAdapter: GroupGamePreStartAdapter

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var gameAdapter: GroupGameListAdapter

    override
    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupGameBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initView

        initViews()
//        initViews()

        /*// init socket manager
        initSocketManager()

        // initCallback
        initSocketCallback()

        // get online game from api
        getOnlineGameData()*/
    }

    private fun initViews() {
        binding?.apply {
            btnCreateGame.setOnClickListener {
                createGameRequest?.let {
                    it()
                }
            }

            // rv
            rv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            rv.adapter = gameAdapter
            gameAdapter.setUserId(userId = userId)



            // join request to game
            gameAdapter.onRequestJoinCallback { gameId ->
                requestJoinToGame?.let {
                    it(gameId)
                }
            }

            // manage creating game
            gameAdapter.onManageClickedCallback {
                /*val action = GroupGameFragmentDirections.actionGlobalGroupGameFragment(
                    channelId = channelId,
                    token = token,
                    creatorId = userId,
                    userId = userId,
                    gameId = it
                )
                findNavController().navigate(action)
                // off socket
                vm.turnOffChannelSocket()*/
            }

        }
    }

    fun getOnlineGame(data: List<ChannelGameData>) {
        lifecycleScope.launch {
            if (data.isNotEmpty()) binding?.apply {
                // if user has created game
                data.find {
                    it.creatorId == userId
                }.apply {
                    // you're not creator game
                    if (this == null) hasCreatedGame = false
                    else {
                        hasCreatedGame = true
                        btnCreateGame.also {
                            it.isEnabled = false
                            it.visibility = View.GONE
                        }
                    }
                }
                data.find {
                    !it.finished && !it.started && it.creatorId != userId
                }?.apply {
                    this.users.find { find ->
                        find.userId == userId && find.accepted
                    }?.let {
                        /*if (!alreadySearchedMe) {
                            dialogManager.simpleMessageConfirmable(msg = "شما در داخل یکی از بازی ها هستید قصد ادامه دارید ؟") { confirm ->
                                if (confirm) {
                                    // off socket
                                    vm.turnOffChannelSocket()

                                }
                                // exit from pre game
                                else {
                                    vm.leaveChannelGame(gameId = this.gameId)
                                    alreadySearchedMe = false
                                }
                            }

                            alreadySearchedMe = true
                        }*/
                    }
                }

                // bind to rv
                gameAdapter.addItem(newItem = data)

            } else {
                binding?.apply {
                    // no game creating
                    hasCreatedGame = false
                }
            }
        }
    }

    private var createGameRequest: (() -> Unit)? = null
    fun createGameRequestCallback(it: () -> Unit) {
        createGameRequest = it
    }

    private var requestJoinToGame:((String)->Unit)?=null
    fun requestJoinToGameCallback(it:(gameId:String)->Unit){
        requestJoinToGame = it
    }

    /*private fun getArgs() {
        token = navArgs.token
        channelId = navArgs.channelId
        vm.setChannelId(channelId = channelId)
        userId = navArgs.userId
        creatorId = navArgs.creatorId
        gameId = navArgs.gameId
    }


    private fun getOnlineGameData() {
        vm.getOnlineGamePreStartUpdate(channelId = channelId, gameId = gameId)
    }

    private fun initSocketCallback() {
        vm.initChannelSocket(callback = this)
    }

    private fun initSocketManager() {
        vm.setChannelSocket(channelSocketManager = SocketManager.getChannelSocketManager())
    }

    private fun initViews() {
        binding?.apply {

            // moderator panel
            if (creatorId == userId) {
                showModeratorPanel()
            }

            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv.adapter = inGameUsersAdapter
            inGameUsersAdapter.setUserId(userId = userId)
            inGameUsersAdapter.setCreatorId(creatorId = creatorId)

            cardSelectedList.setOnClickListener {
                confirmedPage = true
                // update rv
                inGameUsersAdapter.setPage(requisition = false)
                users.filter {
                    it.accepted
                }.apply {
                    inGameUsersAdapter.modifierItem(newList = this)
                }
                cardSelectedList.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red500
                    )
                )

                cardRequestedList.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey500
                    )
                )
            }

            cardRequestedList.setOnClickListener {
                confirmedPage = false
                // update rv
                inGameUsersAdapter.setPage(requisition = true)
                users.filterNot {
                    it.accepted
                }.apply {
                    inGameUsersAdapter.modifierItem(newList = this)
                }
                cardSelectedList.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey500
                    )
                )
                cardRequestedList.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red500
                    )
                )
            }

            // confirm or deni user
            inGameUsersAdapter.onUserDeniCallback { state, userId, requisition ->
                if (requisition) {
                    vm.kickUserFromRegisterGame(gameId = gameId, userId = userId)
                } else {
                    vm.confirmOrDeniUser(gameId = gameId, requesterId = userId, accept = state)
                }

            }
            inGameUsersAdapter.onUserAcceptCallback { state, userId ->
                vm.confirmOrDeniUser(gameId = gameId, requesterId = userId, accept = state)
            }

            // check ready
            fabCheckReady.setOnClickListener {
                vm.checkReady(gameId = gameId)
            }

            // start game
            fabStartGame.setOnClickListener {
                vm.startChannelGame(gameId = gameId)
            }
        }
    }

    private fun showModeratorPanel() {
        binding?.apply {
            lifecycleScope.launch {
                val slideTop = Slide(Gravity.TOP).apply {
                    duration = 500
                }
                TransitionManager.beginDelayedTransition(parent, slideTop)
                layerModeratorPanelOne.visibility = View.VISIBLE
                delay(500)
                val slideBottom = Slide(Gravity.BOTTOM).apply {
                    duration = 500
                }
                TransitionManager.beginDelayedTransition(parent, slideBottom)
                layerModeratorPanelTwo.visibility = View.VISIBLE
            }
        }
    }

    private fun showExitDialog() {
        dialogManager.dialogExitChannelGame {
            if (it) {
                // leave channel game
                vm.leaveChannelGame(gameId = gameId)
                // off socket
                vm.turnOffSockets()
                // navigate
                findNavController().navigate(R.id.action_groupGameFragment_pop)
            }
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOnlineGameUpdate(data: List<OnlineGameUpdatePreStartEntity.OnLineGameUpdatePreStartData>) {
        /*users.clear()
        users.addAll(data)
        lifecycleScope.launch {
            binding?.apply {
                if (data.isNotEmpty()) {
                    // choose which page update
                    // update confirmed rv users
                    users.filterNot {
                        it.accepted
                    }.apply {
                        if (this.isNotEmpty()) {
                            txtRequestCount.visibility = View.VISIBLE
                            txtRequestCount.text = (this.size).toString()
                        } else {
                            txtRequestCount.visibility = View.INVISIBLE
                            txtRequestCount.text = ""
                        }
                    }
                    if (confirmedPage) users.filter {
                        it.accepted
                    }.apply {
                        inGameUsersAdapter.modifierItem(newList = this)
                    }
                    // update unconfirmed rv users
                    else data.filterNot {
                        it.accepted
                    }.apply {
                        inGameUsersAdapter.modifierItem(newList = this)
                    }

                } else {
                    txtRequestCount.visibility = View.INVISIBLE
                    txtRequestCount.text = ""
                }
            }
        }*/
    }

    override fun onCheckReady() {
        /*lifecycleScope.launch {
            dialogManager.dialogCheckReady {
                vm.readyCheckStatus(status = it, gameId = gameId)
            }
        }*/
    }

    override fun onChannelGameStart() {
        /*lifecycleScope.launch {
            val bs = FindMatchBsFragment(MatchType.RANKED, ScenariosEnum.NATO,fromChannel = true)
            bs.show(childFragmentManager, null)
            // TODO store game state
            bs.gameFoundCallback { _, isCreator ->
                if (isCreator) {
                    val action = NatoFragmentDirections.actionToGameNatoFragment(
                        character = null,
                        userId = userId,
                        joinType = "moderator",
                        roomId = null,
                        usersData = null,
                        fromReconnect = false,
                        hasGameEvent = false
                    )
                    findNavController().navigate(action)
                } else findNavController().navigate(R.id.action_global_selectCharacterFragment)
            }
            // turn off socket
            vm.turnOffSockets()
        }*/
    }
}