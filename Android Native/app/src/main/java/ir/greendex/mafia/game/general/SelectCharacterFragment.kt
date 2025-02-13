package ir.greendex.mafia.game.general

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentSelectCharacterBinding
import ir.greendex.mafia.entity.game.general.UserQueueToPickEntity
import ir.greendex.mafia.entity.game.general.enum_cls.ScenariosEnum
import ir.greendex.mafia.game.adapter.general.SelectCharacterAdapter
import ir.greendex.mafia.game.adapter.general.UserQueueToSelectCharacterAdapter
import ir.greendex.mafia.game.general.listener.SelectCharacterListener
import ir.greendex.mafia.game.vm.general.SelectCharacterVm
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.SELECT_CHARACTER_INTERVAL
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.socket.SocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SelectCharacterFragment : BaseFragment(), SelectCharacterListener {
    private var _binding: FragmentSelectCharacterBinding? = null
    private val binding get() = _binding
    private val vm: SelectCharacterVm by viewModels()
    private val args:SelectCharacterFragmentArgs by navArgs()
    private var timer: CountDownTimer? = null
    private lateinit var myUserId: String
    private lateinit var scenariosEnum: ScenariosEnum

    @Inject
    lateinit var userQueueAdapter: UserQueueToSelectCharacterAdapter

    @Inject
    lateinit var selectCharacterAdapter: SelectCharacterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // store routing
        storeRouting()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.GAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectCharacterBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set socket
        setSocket()

        // get user id
        getUserId()

        // get selected scenario
        getSelectedScenario()

        // initViews
        initViews()

        // ready to choose
        readyToChoose()

        // get users turn
        getUsersTurn()

        // getCharacters
        getCharacters()

        // your turn
        yourTurnToPick()

        // random character
        randomCharacter()

        // abandon()
        abandon()
    }

    private fun abandon() {
        vm.abandonSelection()
    }

    private fun setSocket() {
        vm.setSocket(
            findMatchSocketManager = SocketManager.getFindMatchSocketManager(),
            selectCharacterListener = this
        )
    }

    private fun getUserId() {
        MainActivity.userId?.let {
            myUserId = it
        }
    }

    private fun getSelectedScenario() {
        vm.getMatchScenario {
            this.scenariosEnum = it
        }
    }

    private fun randomCharacter() {
        vm.randomCharacter()
    }

    private fun yourTurnToPick() {
        vm.yourTurnToPick()
    }

    private fun getUsersTurn() {
        vm.usersTurnToPick()
    }

    private fun filterQueueList(queue: List<UserQueueToPickEntity.UserQueueToPickData>): Flow<List<UserQueueToPickEntity.UserQueueToPickData>> {
        return flow {
            val list = mutableListOf<UserQueueToPickEntity.UserQueueToPickData>()
            queue.forEachIndexed { index, userQueueToPickData ->
                if (index == 0) list.add(
                    UserQueueToPickEntity.UserQueueToPickData(
                        userName = "انتخاب",
                        userId = userQueueToPickData.userId,
                        userImage = userQueueToPickData.userImage
                    )
                )
                else list.add(
                    UserQueueToPickEntity.UserQueueToPickData(
                        userName = userQueueToPickData.userName,
                        userId = userQueueToPickData.userId,
                        userImage = userQueueToPickData.userImage
                    )
                )
            }
            emit(list)
        }.flowOn(Dispatchers.IO)
    }

    private fun getCharacters() {
        vm.getCharacters { character ->
            lifecycleScope.launch {
                // add item to rv
                selectCharacterAdapter.addItem(character)
            }
        }
    }


    private fun readyToChoose() {
        vm.readyToChooseCharacter()
    }

    private fun initViews() {
        binding?.apply {

            // rv user queue
            rvQueue.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvQueue.adapter = userQueueAdapter

            // rv characters
            rvCharacters.layoutManager =
                FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.SPACE_AROUND
                    flexWrap = FlexWrap.WRAP
                }

            rvCharacters.adapter = selectCharacterAdapter

            // rv character callback
            selectCharacterAdapter.selectedCharacterCallback { index, character ->
                lifecycleScope.launch {
                    if (index == null) {
                        msg("این کارت قبلا انتخاب شده")
                    } else {
                        // stop active timer
                        timer?.cancel()
                        //send to server selected character
                        vm.selectCharacter(index)
                        // show to user selected character
                        showCharacterBs(character = character!!)
                    }
                }
            }

            // my turn pick error
            selectCharacterAdapter.myTurnErrorCallback {
                msg("هنوز نوبت انتخاب شما نشده")
            }
        }
    }

    private fun showCharacterBs(character: String) = lifecycleScope.launch {
        val bs = ShowCharacterBsFragment(character = character, scenariosEnum = scenariosEnum)
        // bind to active bottom sheet
        MainActivity.activeBottomSheet = bs
        bs.isCancelable = false
        bs.show(childFragmentManager, null)
        // navigation to game page
        bs.callbackNavigation {
            // navigation to game
            val action = SelectCharacterFragmentDirections.actionToGameNatoFragment(
                character = character,
                userId = myUserId,
                joinType = "player",
                fromReconnect = false,
                roomId = null,
                usersData = null,
                gameId = args.gameId
            )
            findNavController().navigate(
                action
            )
            // turn off listeners
            vm.offListeners()
        }
    }

    override fun onYourTurnToPick() {
        lifecycleScope.launch {
            // set permission to user to choose card
            selectCharacterAdapter.setMyTurn()
            // toast
            longMsg("نوبت شماست")
        }
    }

    override fun onRandomCharacter(it: String) {
        lifecycleScope.launch {
            // show to user
            showCharacterBs(it)
        }
    }

    override fun onUsersTurnToPick(it: List<UserQueueToPickEntity.UserQueueToPickData>) {
        lifecycleScope.launch {
            binding?.apply {
                lifecycleScope.launch {
                    // disable active timer
                    if (timer != null) {
                        progress.progress = 0f
                        timer?.cancel()
                    }
                    // on selection user turn
                    txtUserName.text = it[0].userName
                    imgUser.load(BASE_URL.plus(it[0].userImage))
                    // rv queue
                    filterQueueList(it).collect {
                        userQueueAdapter.addItem(it as MutableList<UserQueueToPickEntity.UserQueueToPickData>)
                    }

                    timer = object : CountDownTimer(SELECT_CHARACTER_INTERVAL, 1000L) {
                        override fun onTick(millisUntilFinished: Long) {
                            progress.progress = (millisUntilFinished / 1000L).toFloat()
                        }

                        override fun onFinish() {}

                    }.start()
                }
            }
        }
    }

    override fun onAbandon() {
        lifecycleScope.launch {
            findNavController().navigate(R.id.action_global_homeFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}