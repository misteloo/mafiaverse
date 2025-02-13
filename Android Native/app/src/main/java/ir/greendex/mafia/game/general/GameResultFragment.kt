package ir.greendex.mafia.game.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentGameResultBinding
import ir.greendex.mafia.entity.game.general.EndGameFreeSpeechEntity
import ir.greendex.mafia.entity.game.general.EndGameResultEntity
import ir.greendex.mafia.entity.game.general.GameResultUserFreeSpeechEntity
import ir.greendex.mafia.game.adapter.general.GameResultAdapter
import ir.greendex.mafia.game.adapter.general.GameResultFreeSpeechAdapter
import ir.greendex.mafia.game.general.listener.EndGameVmListener
import ir.greendex.mafia.game.vm.general.GameResultVm
import ir.greendex.mafia.util.base.BaseFragment
import ir.greendex.mafia.util.socket.SocketManager
import ir.samanjafari.easycountdowntimer.CountDownInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class GameResultFragment : BaseFragment(), EndGameVmListener {
    private var _binding: FragmentGameResultBinding? = null
    private val binding get() = _binding
    private var gameResult: EndGameResultEntity.EndGameResultData? = null
    private var roomId: String? = null
    private val vm: GameResultVm by viewModels()
    private val users by lazy { mutableListOf<EndGameResultEntity.EndGameResultUsers>() }
    private val speakers by lazy { mutableListOf<GameResultUserFreeSpeechEntity>() }

    // injection
    @Inject
    lateinit var adapter: GameResultAdapter

    @Inject
    lateinit var freeSpeechAdapter: GameResultFreeSpeechAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameResult = arguments?.getParcelable("result")
        roomId = arguments?.getString("roomId")
        gameResult?.let {
            CoroutineScope(Dispatchers.IO).launch {
                users.addAll(it.users)
            }

        }
        // set room
        roomId?.let {
            vm.initRoom(roomId = it)
        }
        // set socket
        vm.setSocket(endGameSocketManager = SocketManager.getEndGameSocket())
        // start to listen
        vm.initSocket(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameResultBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initViews
        initViews()

    }

    private fun initViews() {
        binding?.apply {
            // timer
            gameResult?.let {
                // set font
                countDownTimer.setTypeFace(ResourcesCompat.getFont(requireContext(), R.font.yekan))
                // set time
                countDownTimer.setTime(
                    0,
                    0,
                    50,
                    0
                )
                countDownTimer.setOnTick(object : CountDownInterface {
                    override fun onTick(time: Long) {}

                    override fun onFinish() {
                        // off socket
                        /*vm.turnOffSocket()
                        vm.setMicStatus(false)
                        fabMic.isEnabled = false
                        findNavController().navigate(R.id.action_global_homeFragment)*/
                    }
                })
                countDownTimer.startTimer()
            }

            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val divider = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.style_divier)!!)
            rv.addItemDecoration(divider)
            rv.adapter = adapter

            rvSpeech.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            rvSpeech.adapter = freeSpeechAdapter

            // set rv
            gameResult?.let {
                adapter.modifierItem(newItem = it.users)
            }

            // mic
            vm.micStatusLiveData.observe(viewLifecycleOwner) {
                micToggleUi(status = it)
            }

            // mic toggle
            fabMic.setOnClickListener {
                vm.setMicStatus(!vm.getMicStatus())
            }


            // exit
            fabExit.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO){
                    // dc room
                    vm.disConnectRoom()
                    // off socket
                    vm.turnOffSocket()
                    // stop timer
                    countDownTimer.pause()
                    // disable open mic
                    vm.setMicStatus(status = false)
                    withContext(Dispatchers.Main){
                        val bundle = Bundle().apply {
                            putBoolean("join_request", true)
                        }
                        findNavController().navigate(R.id.action_gameResultFragment_to_homeFragment,bundle)
                    }
                }
            }
        }
    }

    private fun micToggleUi(status: Boolean) {
        binding?.apply {
            if (status) {
                fabMic.icon =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_mic_off_24
                    )
                fabMic.text = "خاموش"
            } else {
                fabMic.icon =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_mic_24
                    )
                fabMic.text = "روشن"
            }
        }
    }

    override fun onSpeech(data: List<EndGameFreeSpeechEntity.EndGameFreeSpeechData>) {
        lifecycleScope.launch(Dispatchers.IO) {
            CoroutineScope(Dispatchers.IO).launch {
                val speakersNew = async {
                    for (i in data.indices) {
                        val item = data[i]
                        if (item.talking) {
                            speakers.find {
                                it.userId == item.userId
                            }.also { result ->
                                if (result == null) {
                                    users.find {
                                        it.userId == item.userId
                                    }?.also { user ->
                                        speakers.add(
                                            GameResultUserFreeSpeechEntity(
                                                userId = user.userId,
                                                userName = user.userName,
                                                userImage = user.userImage,
                                                isTalking = true
                                            )
                                        )
                                    }
                                }
                            }

                        } else {
                            speakers.find {
                                it.userId == item.userId
                            }?.apply {
                                speakers.indexOfFirst {
                                    it.userId == item.userId
                                }.also { index ->
                                    speakers.removeAt(index)
                                }
                            }
                        }
                    }
                    speakers
                }
                withContext(Dispatchers.Main) {
                    freeSpeechAdapter.modifierItem(newList = speakersNew.await())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}