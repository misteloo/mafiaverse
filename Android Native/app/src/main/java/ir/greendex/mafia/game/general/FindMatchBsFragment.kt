package ir.greendex.mafia.game.general

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentFindMatchBsBinding
import ir.greendex.mafia.entity.game.general.FindMatchUserEntity
import ir.greendex.mafia.entity.game.general.enum_cls.ScenariosEnum
import ir.greendex.mafia.game.adapter.general.FindMatchAdapter
import ir.greendex.mafia.game.vm.general.FindMatchBsVm
import ir.greendex.mafia.util.MatchType
import ir.greendex.mafia.util.socket.SocketManager
import ir.greendex.mafia.util.sound.SoundManager
import ir.greendex.mafia.util.vibrate.Vibrate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FindMatchBsFragment(
    private val matchType: MatchType,
    private val scenariosEnum: ScenariosEnum,
    private val fromChannel: Boolean = false
) : BottomSheetDialogFragment() {
    private var _binding: FragmentFindMatchBsBinding? = null
    private val binding get() = _binding
    private val vm: FindMatchBsVm by viewModels()

    // injection
    @Inject
    lateinit var adapter: FindMatchAdapter

    @Inject
    lateinit var vibrate: Vibrate

    @Inject
    lateinit var soundManager: SoundManager

    override
    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindMatchBsBinding.inflate(layoutInflater)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED

        // set socket
        setSocket()

        // initView
        initViews()

        // start find match
        findMatch()

        // game found
        gameFound()


    }

    private fun setSocket() {
        vm.setSocket(findMatchSocketManager = SocketManager.getFindMatchSocketManager())
    }

    private var gameFound: ((ScenariosEnum, isCreator: Boolean, gameId: String) -> Unit)? = null
    fun gameFoundCallback(it: (ScenariosEnum, isCreator: Boolean, gameId: String) -> Unit) {
        gameFound = it
    }

    private fun gameFound() {
        vm.gameFound {
            // create notification
            vm.createNotification("مافیا ورس", "بازی شما ایجاد شد")
            lifecycleScope.launch(Dispatchers.Main) {
                binding?.apply {
                    // vibrate
                    vibrate.longVibrate()
                    // play sound
                    soundManager.gameFoundSound()
                    // disable cancel button
                    btnCancel.isEnabled = false
                    btnCancel.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey500
                        )
                    )
                    object : CountDownTimer(8000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            btnCancel.text = (millisUntilFinished / 1000L).toString()
                        }

                        override fun onFinish() {
                            // game found callback
                            gameFound?.let { let ->
                                let(scenariosEnum, it.isCreator, it.gameId)
                            }
                            dismiss()
                        }

                    }.start()
                }
            }
        }
    }


    private fun findMatch() {
        // stop send match type when start from channel
        if (!fromChannel) vm.findMatch(matchType.name.lowercase(), auth = MainActivity.userAuth)
        // server response => user joined callback
        vm.normalRobotUserJoinedFindMatch {
            lifecycleScope.launch(Dispatchers.Main) {
                adapter.addItem(it as MutableList<FindMatchUserEntity.Data>)
            }
        }
    }

    private fun initViews() {
        binding?.apply {
            if (fromChannel) btnCancel.isEnabled = false

            // dismiss finding
            btnCancel.setOnClickListener {
                vm.leaveFindMatch()
                dismiss()
            }

            // rv
            rv.layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            rv.adapter = adapter


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}