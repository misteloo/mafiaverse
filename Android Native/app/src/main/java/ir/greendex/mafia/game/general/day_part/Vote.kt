package ir.greendex.mafia.game.general.day_part

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

class Vote @Inject constructor(
    private val context: Context
) {
    private lateinit var binding: FragmentNatoBinding
    private val handRiseTimer by lazy { mutableListOf<String>() }
    fun initBinding(binding: FragmentNatoBinding) {
        this.binding = binding
    }


    suspend fun userHandRise(users: List<GameActionEntity.GameActionData>) =
        CoroutineScope(Dispatchers.IO).launch {
            users.filter { filter ->
                filter.userAction.voteHandRise
            }.onEach { user ->
                NatoFragment.inGameUsers.find {
                    user.userId == it.userId
                }?.let { let ->
                    // user does not exist
                    if (!handRiseTimer.contains(let.userId)) {
                        val layer = UserLayer.getLayerArray[let.userIndex]
                        withContext(Dispatchers.Main) {
                            handRiseUpdateUi(user = let, layer = layer)
                        }
                    }
                }
            }
        }


    private fun handRiseUpdateUi(
        user: InGameUsersDataEntity.InGameUserData,
        layer: LayerPlayerDayBinding
    ) {
        // add to timer
        handRiseTimer.add(user.userId)
        layer.apply {
            btnHandRise.visibility = View.VISIBLE
            txtUsername.alpha = 0f
            handRiseTimer(layer = layer) {
                btnHandRise.visibility = View.GONE
                txtUsername.setTextColor(ContextCompat.getColor(context, R.color.textMainColor))
                txtUsername.text = user.userName
                txtUsername.alpha = 1f
                handRiseTimer.remove(user.userId)
            }
        }
    }

    private fun handRiseTimer(layer: LayerPlayerDayBinding, timeUp: () -> Unit) {
        layer.apply {
            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    timeUp()
                }
            }.start()
        }
    }

}