package ir.greendex.mafia.game.general.day_part

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.databinding.LayerPlayerDayBinding
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.UsersChallengeStatusEntity
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.event.UserLayer
import ir.greendex.mafia.util.CHALLENGE_REQUEST_INTERVAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Challenge @Inject constructor(
    private val context: Context
) {
    private lateinit var binding: FragmentNatoBinding
    private var challengeAcceptable = false
    private var mySpeechTurn = false
    private var challengeStatusFromServer = false
    private var hasActiveTimer = false
    private lateinit var challengeTimer: CountDownTimer
    private lateinit var myChallengeTimer:CountDownTimer
    fun initBinding(binding: FragmentNatoBinding) {
        this.binding = binding
    }

    fun onClickLayerArray() {
        CoroutineScope(Dispatchers.Main).launch {
            UserLayer.getLayerArray.forEach {
                it.btnChallenge.setOnClickListener { _ ->
                    if (challengeAcceptable) {
                        // disable another challenge to prevent twice selection
                        challengeAcceptable = false
                        onAcceptChallenge?.let { callback ->
                            callback(it.root.tag.toString())
                        }
                    }
                }
            }
        }
    }

    fun setChallengeAcceptable(acceptable: Boolean) {
        challengeAcceptable = acceptable
        mySpeechTurn = acceptable
    }

    suspend fun getUserChallenges(challenge: List<GameActionEntity.GameActionData>) =
        CoroutineScope(Dispatchers.IO).launch {
            for (i in challenge.indices) {
                val it = challenge[i]
                NatoFragment.inGameUsers.find { find ->
                    it.userId == find.userId
                }?.let { let ->
                    val layer = UserLayer.getLayerArray[let.userIndex]
                    withContext(Dispatchers.Main) {
                        updateUi(layer = layer, data = it.userAction)
                    }
                }
            }
        }

    private fun updateUi(layer: LayerPlayerDayBinding, data: GameActionEntity.UserGameAction) {
        layer.apply {
            if (data.challengeRequest) {
                if (mySpeechTurn) {
                    btnChallenge.backgroundTintList =
                        ColorStateList.valueOf(Color.RED)
                } else {
                    btnChallenge.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey500))
                }
                btnChallenge.visibility = View.VISIBLE
                txtUsername.alpha = 0f
                // timer
                challengeTimer(layer = layer)
            }


            if (data.challengeAccept) {
                // change color after challenge accepted
                btnChallenge.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green800))
            }
        }
    }

    private fun challengeTimer(layer: LayerPlayerDayBinding) {
        challengeTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                layer.apply {
                    btnChallenge.visibility = View.GONE
                    txtUsername.alpha = 1f
                    btnChallenge.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.grey500
                        )
                    )
                }
            }
        }.start()
    }

    fun setChallengeStatus(challengeList: List<UsersChallengeStatusEntity.UserChallengeStatusData>) {
        challengeList.find {
            it.userId == NatoFragment.myUserId
        }?.let {
            binding.apply {
                challengeStatusFromServer = it.status
                setChallengeStatusToButton(status = challengeStatusFromServer)
            }
        }
    }

    private fun setChallengeStatusToButton(status: Boolean) {
        binding.layerAnimAction.apply {
            fabChallengeRequest.apply {
                isEnabled = status
            }
            if (hasActiveTimer){
                myChallengeTimer.cancel()
                progressChallenge.progress = 0f
            }
        }
    }

    fun startChallengeReqTimer() {
        binding.layerAnimAction.apply {
            myChallengeTimer = object : CountDownTimer(CHALLENGE_REQUEST_INTERVAL, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progressChallenge.progress = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {
                    hasActiveTimer = false
                    progressChallenge.progress = 0f
                    fabChallengeRequest.isEnabled = challengeStatusFromServer
                }

            }.start()
            hasActiveTimer = true
        }
    }

    private var onAcceptChallenge: ((String) -> Unit)? = null
    fun onAcceptChallengeCallback(it: (String) -> Unit) {
        onAcceptChallenge = it
    }
}