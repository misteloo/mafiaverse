package ir.greendex.mafia.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentGroupBinding
import ir.greendex.mafia.databinding.LayerChannelCreateGameBinding
import ir.greendex.mafia.entity.channel.ChannelGameData
import ir.greendex.mafia.entity.channel.ChannelGameUpdateEntity
import ir.greendex.mafia.entity.channel.ChannelMessageEntity
import ir.greendex.mafia.ui.group.adapter.GroupTabAdapter
import ir.greendex.mafia.ui.group.listeners.ChannelVmListener
import ir.greendex.mafia.ui.group.tap.GroupChatFragment
import ir.greendex.mafia.ui.group.tap.GroupGameFragment
import ir.greendex.mafia.ui.group.tap.GroupGameHistoryFragment
import ir.greendex.mafia.ui.group.vm.GroupVm
import ir.greendex.mafia.util.CHANNEL_GAME_EASY_MODE
import ir.greendex.mafia.util.CHANNEL_GAME_HARD_MODE
import ir.greendex.mafia.util.CHANNEL_GAME_MEDIUM_MODE
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.dialog.DialogManager
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import ir.greendex.mafia.util.socket.SocketManager
import ir.greendex.mafia.util.sound.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GroupFragment : BaseFragment(), ChannelVmListener {
    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding
    private val navArgs: GroupFragmentArgs by navArgs()
    private val vm: GroupVm by viewModels()
    private val dialogManager by lazy { DialogManager(context, soundManager) }
    private val channelGameList by lazy { mutableListOf<ChannelGameData>() }
    private val channelMessage by lazy { mutableListOf<ChannelMessageEntity.ChannelMessageData>() }
    private val channelMessageDB by lazy { mutableListOf<ChannelMessageEntity.ChannelMessageData>() }
    private val channelMessageServer by lazy { mutableListOf<ChannelMessageEntity.ChannelMessageData>() }
    private var dbMessageStartOffset = 0
    private var selectedTabPosition = 1
    private var hasCreatedGame = false
    private var fragmentCreated = false
    private var alreadySearchedMe = false
    private var tabLayoutGenerated = false
    private lateinit var chatFrag: GroupChatFragment
    private lateinit var onlineGameFrag: GroupGameFragment
    private lateinit var gameHistoryFrag: GroupGameHistoryFragment
    private lateinit var token: String
    private lateinit var channelId: String
    private lateinit var userId: String

    // inject

    @Inject
    lateinit var soundManager: SoundManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // args
        getArgs()

        chatFrag = GroupChatFragment()
        onlineGameFrag =
            GroupGameFragment(channelId = this.channelId, token = this.token, userId = this.userId)
        gameHistoryFrag = GroupGameHistoryFragment()

        // get all message size from db
        getAllMessageSize()

        // start channel socket
        channelSocket()

        // channel listener
        initChannelSocketCallback()

        fragmentCreated = true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initViews
        initViews()

        // get local messages
        getMessagesFromDbFirstInitialize()

        // join to channel
        joinToChannel()

        // get online games
        getChannelOnlineGames()

        // get unreadMessages
        getUnreadMessages()

    }

    private fun getUnreadMessages() {
        vm.getSpecificChannel(id = navArgs.groupId, userToken = navArgs.token) {
            if (it.content.isNotEmpty()) {
                channelMessageServer.clear()
                for (channelContent in it.content) {
                    channelMessageServer.add(
                        ChannelMessageEntity.ChannelMessageData(
                            messageId = channelContent.msgId,
                            userId = channelContent.userId,
                            userName = channelContent.userName,
                            userImage = channelContent.userImage,
                            message = channelContent.msg,
                            messageType = channelContent.msgType,
                            messageTime = channelContent.time,
                            userState = channelContent.userState
                        )
                    )
                }

                // bind
                chatFrag.bindMessagesToRv(messageList = channelMessageServer, fromLocal = false)
            }
        }
    }

    private fun getAllMessageSize() {
        vm.getAllMessageSizeFromDb {
            chatFrag.dbMessageSize(it)
        }
    }

    private fun joinToChannel() {
        vm.joinToChannel(channelId = channelId)
    }

    private fun initChannelSocketCallback() {
        vm.initChannelSocket(callback = this)
    }

    private fun channelSocket() {
        vm.setChannelSocket(channelSocketManager = SocketManager.getChannelSocketManager())
    }

    private fun getArgs() {
        token = navArgs.token
        channelId = navArgs.groupId
        userId = navArgs.userId
        // set channel id
        vm.setChannelId(channelId = channelId)

    }

    private fun initViews() {
        binding?.apply {
            // tab layout
            generateTabLayout()
            // img group
            imgGroup.load(R.drawable.test)
            txtGroupName.text = navArgs.groupName

            // back
            imgBack.setOnClickListener {
                // exit channel
                vm.exitFromCurrentChannel()
                // disable sockets
                vm.turnOffChannelSocket()

                findNavController().popBackStack()
            }

            // group detail
            groupHeader.setOnClickListener {
                // exit channel
                vm.exitFromCurrentChannel()
                val action =
                    GroupDetailFragmentDirections.actionGlobalGroupDetailFragment(channelId = channelId)
                findNavController().navigate(action)
//                vm.turnOffChannelSocket()
            }


            // callback chat frag
            chatFrag.getLocalMessageCallback {
                getLocalMessage(offset = it, scrollToEnd = false)
            }

            chatFrag.sendMsgCallback {
                vm.sendMessage(message = it)
            }

            // callback online game
            onlineGameFrag.createGameRequestCallback {
                createGame()
            }

            onlineGameFrag.requestJoinToGameCallback {
                vm.joinToGame(gameId = it)
            }
        }
    }

    private fun generateTabLayout() {
        lifecycleScope.launch {
            val list = listOf(gameHistoryFrag, chatFrag, onlineGameFrag)
            binding?.apply {
                val job = CoroutineScope(Dispatchers.Main).launch {
                    val adapter = GroupTabAdapter(childFragmentManager, lifecycle)
                    adapter.addFragments(list)
                    vp.adapter = adapter
                    TabLayoutMediator(tab, vp) { tab, position ->
                        when (position) {
                            0 -> {
                                tab.setIcon(R.drawable.round_history_24)
                                tab.text = "سوابق بازی"
                            }

                            1 -> {
                                tab.setIcon(R.drawable.round_chat_bubble_24)
                                tab.text = "گفتوگو"
                            }

                            2 -> {
                                tab.setIcon(R.drawable.ic_hat_grey)
                                tab.text = "بازی آنلاین"

                            }

                        }

                    }.attach()
                }
                job.join()
                tab.getTabAt(selectedTabPosition)?.select()
                tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {

                        tab?.let {
                            selectedTabPosition = it.position
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }

                })
            }
        }
    }

    private fun createGame() {
        val layer = LayerChannelCreateGameBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(requireContext()).apply {
            setCancelable(true)
            setContentView(layer.root)
        }
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
        var price = 0
        var asModerator = false
        layer.apply {
            btnCreateGame.setOnClickListener {
                if (txtSelectedGold.text == "انتخاب نشده") {
                    longMsg("سکه ورودی رو مشخص  کن")
                    return@setOnClickListener
                }
                btnCreateGame.loading(progress = progress)

                // user has enough gold for creating game ?!
                vm.userHasEnoughGold(
                    entireGold = price, token = token
                ) {
                    btnCreateGame.hideLoading(progress = progress)
                    if (!it) {
                        longMsg("سکه کافی ندارید")
                    }
                    // game able to create
                    else {
                        // send to server
                        vm.createGame(entireGold = price, moderator = asModerator)
                        dialog.dismiss()
                    }
                }
            }

            fabClose.setOnClickListener {
                dialog.dismiss()
            }

            btnGoldEasy.setOnClickListener {
                txtSelectedGold.text = CHANNEL_GAME_EASY_MODE.plus(" سکه ")
                price = 100
            }
            btnGoldMedume.setOnClickListener {
                txtSelectedGold.text = CHANNEL_GAME_MEDIUM_MODE.plus(" سکه ")
                price = 150
            }
            btnGoldHard.setOnClickListener {
                txtSelectedGold.text = CHANNEL_GAME_HARD_MODE.plus(" سکه ")
                price = 200
            }
            switchMod.setOnCheckedChangeListener { _, isChecked ->
                asModerator = isChecked
            }
        }

    }

    private fun getMessagesFromDbFirstInitialize() {
        // get local message from db
        if (channelMessageDB.isEmpty()) {
            getLocalMessage(offset = dbMessageStartOffset, true)
        }
    }

    private fun getLocalMessage(offset: Int, scrollToEnd: Boolean) {
        vm.getLocalMessage(offset = offset) {
            channelMessageDB.addAll(it)
            chatFrag.bindMessagesToRv(messageList = it, scrollToEnd = scrollToEnd, fromLocal = true)
        }
    }


    override fun onChannelMessageReceived(data: ChannelMessageEntity.ChannelMessageData) {
        chatFrag.bindMessagesToRv(message = data, fromLocal = false)
    }

    override fun onChannelGame(data: List<ChannelGameData>) {
        channelGameList.clear()
        channelGameList.addAll(data)
        onChannelGameBinding(data = channelGameList)
    }

    override fun onUpdateOnlineGame(data: ChannelGameUpdateEntity.ChannelGameUpdateData) {
        lifecycleScope.launch {
            channelGameList.indexOfFirst { it.gameId == data.gameId }.also { index: Int ->
                if (index != -1) {
                    val job = CoroutineScope(Dispatchers.IO).launch {
                        var game = channelGameList[index]
                        game = ChannelGameData(
                            game.gameId,
                            game.creatorId,
                            game.scenario,
                            game.entireGold,
                            game.observable,
                            game.startTime,
                            game.endTime,
                            game.finished,
                            game.started,
                            game.winner,
                            data.users
                        )
                        channelGameList[index] = game
                    }
                    job.join()
//                    onChannelGameBinding(data = channelGameList)
                }
            }
        }
    }

    private fun onChannelGameBinding(data: List<ChannelGameData>) {
        onlineGameFrag.getOnlineGame(data = data)
    }

    private fun getChannelOnlineGames() {
        vm.getChannelOnlineGames()
    }

    override fun onPause() {
        super.onPause()
        fragmentCreated = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}