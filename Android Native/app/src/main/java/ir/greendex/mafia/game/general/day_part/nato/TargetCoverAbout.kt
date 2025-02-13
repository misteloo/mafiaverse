package ir.greendex.mafia.game.general.day_part.nato

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.databinding.LayerPlayerDayBinding
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.event.UserLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TargetCoverAbout @Inject constructor(
    private val context: Context
) {
    private lateinit var binding: FragmentNatoBinding
    private val handRiseTimerArray by lazy { mutableListOf<String>() }
    private var acceptHandRise = false
    private var whichUserRequest = ""
    fun initBinding(binding: FragmentNatoBinding) {
        this.binding = binding
    }

     fun onClickLayerArray() {
        CoroutineScope(Dispatchers.Main).launch {
            UserLayer.getLayerArray.forEach { layer ->
                layer.btnHandRise.setOnClickListener {
                    if (acceptHandRise) {
                        // disable another challenge to prevent twice selection
                        acceptHandRise = false
                        onAcceptHandRise?.let { callback ->
                            callback(layer.root.tag.toString())
                        }
                    }
                }
            }
        }
    }

    fun setWhichUserRequest(whichUserRequest: String) {
        this.whichUserRequest = whichUserRequest
        acceptHandRise = (whichUserRequest == NatoFragment.myUserId)
    }

    suspend fun userActionHistory(usersData: List<GameActionEntity.GameActionData>) {
        // update
        updateUserHandRise(data = usersData)
    }


    private suspend fun updateUserHandRise(data: List<GameActionEntity.GameActionData>) {
        CoroutineScope(Dispatchers.IO).launch {
            data.filter {
                it.userAction.targetCoverHandRise
            }.onEach {
                NatoFragment.inGameUsers.find { find ->
                    it.userId == find.userId
                }?.let {
                    val layer = UserLayer.getLayerArray[it.userIndex]
                    val action = NatoFragment.userActionHistory.find { findAction ->
                        findAction.userId == it.userId
                    }
                    withContext(Dispatchers.Main) {
                        updateUi(user = it, layer = layer, action = action?.userAction)
                    }
                }
            }
        }
    }

    private fun updateUi(
        user: InGameUsersDataEntity.InGameUserData,
        layer: LayerPlayerDayBinding,
        action: GameActionEntity.UserGameAction?
    ) {
        layer.apply {
            if (action != null) {
                if (action.targetCoverHandRise) {
                    handRiseTimerArray.find {
                        it == user.userId
                    }.apply {
                        if (this == null) {
                            handRiseTimerArray.add(user.userId)
                            btnHandRise.visibility = View.VISIBLE
                            txtUsername.alpha = 0f
                            if (whichUserRequest == NatoFragment.myUserId) {
                                btnHandRise.also {
                                    it.setBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.red500
                                        )
                                    )
                                }
                            }
                            // start timer
                            handRiseTimer(layer = layer, userId = user.userId)
                        }
                    }
                }
                if (action.targetCoverAccepted) {
                    btnHandRise.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green800
                        )
                    )
                }
            }
        }
    }

    private fun handRiseTimer(layer: LayerPlayerDayBinding, userId: String) {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                layer.apply {
                    btnHandRise.also {
                        it.visibility = View.GONE
                        it.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.grey500
                            )
                        )
                    }
                    txtUsername.alpha = 1f
                    handRiseTimerArray.remove(userId)
                }
            }
        }.start()
    }

    private var onAcceptHandRise: ((String) -> Unit)? = null
    fun onAcceptHandRiseCallback(it: (String) -> Unit) {
        onAcceptHandRise = it
    }
}