package ir.greendex.mafia.game.general.action_anim

import android.os.CountDownTimer
import androidx.constraintlayout.motion.widget.MotionLayout
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.entity.game.general.enum_cls.AnimActionEnum
import ir.greendex.mafia.entity.game.general.enum_cls.AnimMsgEnum

class InGameAnimHandler(private val binding: FragmentNatoBinding) {

    fun init() {
        binding.apply {
            // anim msg listener
            layerAnimMsg.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int
                ) {

                }

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {

                }

                override fun onTransitionCompleted(
                    motionLayout: MotionLayout?,
                    currentId: Int
                ) {
                    when (currentId) {
                        R.id.openMic -> {
                            layerAnimMsg.transitionToEnd {
                                timerToHideMsgAnim()
                            }
                        }

                        R.id.simpleMsg -> timerToHideMsgAnim()
                    }
                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {

                }

            })
        }
    }

    fun showActionAnim(type: AnimActionEnum) {
        binding.layerAnimEvent.apply {
            when (type) {
                AnimActionEnum.IDLE -> transitionToState(R.id.natoIdle)
                AnimActionEnum.NORMAL -> transitionToState(R.id.natoAction)
                AnimActionEnum.SPEECH -> transitionToState(R.id.natoSpeech)
                AnimActionEnum.VOTE -> transitionToState(R.id.natoVote)
                AnimActionEnum.NIGHT -> transitionToState(R.id.natoNight)
                AnimActionEnum.MODERATOR -> transitionToState(R.id.nato_moderator)
                AnimActionEnum.DEAD -> transitionToState(R.id.natoDead)
            }
        }
    }

    fun showMsgAnim(msg: String? = null, type: AnimMsgEnum) {
        binding.layerAnimMsg.apply {
            if (msg != null) binding.txtMsg.text = msg

            when (type) {
                AnimMsgEnum.MIC -> transitionToState(R.id.openMic)
                AnimMsgEnum.SIMPLE -> transitionToState(R.id.simpleMsg)
                AnimMsgEnum.IDLE -> transitionToState(R.id.natoIdle)
            }
        }
    }

    private fun timerToHideMsgAnim() {
        object : CountDownTimer(2500, 500) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                binding.apply {
                    showMsgAnim(type = AnimMsgEnum.IDLE)
                }
            }
        }.start()
    }
}