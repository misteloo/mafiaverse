package ir.greendex.mafia.game.general.day_part

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.databinding.LayerPlayerDayBinding
import ir.greendex.mafia.entity.game.general.CurrentSpeechEntity
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorStatus
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.event.UserLayer
import ir.greendex.mafia.util.BASE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserStatus @Inject constructor(
    private val context: Context
) {
    private lateinit var speechTimer: CountDownTimer
    private lateinit var binding: FragmentNatoBinding
    fun initBinding(binding: FragmentNatoBinding) {
        this.binding = binding
    }

    suspend fun getUserData(usersData: List<GameActionEntity.GameActionData>) {
        CoroutineScope(Dispatchers.IO).launch {
            // update
            for (i in usersData.indices) {
                val it = usersData[i]
                NatoFragment.inGameUsers.find { find ->
                    find.userId == it.userId
                }?.let { let ->
                    val layer = UserLayer.getLayerArray[let.userIndex]
                    NatoFragment.userActionHistory.find {
                        it.userId == let.userId
                    }?.let { status ->
                        withContext(Dispatchers.Main) {
                            updateActionUi(layer = layer, user = let, status = status)
                        }
                    }
                }
            }
        }
    }

    suspend fun updateSingleActionUi(item: GameActionEntity.GameActionData) {
        NatoFragment.inGameUsers.find { find ->
            find.userId == item.userId
        }?.let { let ->
            val layer = UserLayer.getLayerArray[let.userIndex]
            NatoFragment.userActionHistory.find {
                it.userId == let.userId
            }?.let { status ->
                withContext(Dispatchers.Main) {
                    updateActionUi(layer = layer, user = let, status = status)
                }
            }
        }
    }


    private fun updateActionUi(
        user: InGameUsersDataEntity.InGameUserData,
        layer: LayerPlayerDayBinding,
        status: GameActionEntity.GameActionData
    ) {

        if (!status.userStatus.isAlive) {
            layer.imgUser.load(R.drawable.image_rip)
            return
        }
        if (!status.userStatus.isConnected) {
            layer.imgUser.load(R.drawable.round_wifi_off_24)
            return
        }

        if (!status.userStatus.isTalking) {
            layer.apply {
                animSpeaking.also { anim ->
                    anim.alpha = 0f
                    anim.cancelAnimation()
                }

                if (status.userAction.like) {
                    animLike.also {
                        it.alpha = 1f
                        it.playAnimation()
                    }
                    // timer
                    likeDislikeTimer(layer = layer)
                    return
                }
                if (status.userAction.dislike) {
                    animDislike.also {
                        it.alpha = 1f
                        it.playAnimation()
                    }

                    // timer
                    likeDislikeTimer(layer = layer)
                    return
                }

                layer.apply {
                    imgUser.load(BASE_URL.plus(user.userImage))
                }
            }
        }
    }

    suspend fun getCurrentSpeech(currentUser: CurrentSpeechEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            NatoFragment.inGameUsers.find {
                it.userId == currentUser.currentUserId
            }?.apply {
                withContext(Dispatchers.Main) {
                    updateSpeechUi(
                        user = this@apply,
                        layer = UserLayer.getLayerArray[this@apply.userIndex],
                        timer = currentUser.timer
                    )
                }

            }
        }
    }

    private fun updateSpeechUi(
        user: InGameUsersDataEntity.InGameUserData,
        layer: LayerPlayerDayBinding,
        timer: Int
    ) {
        layer.apply {
            // show speaking anim
            animSpeaking.also { anim ->
                anim.alpha = 1f
                anim.playAnimation()
            }
            // body animation
            binding.layoutDayAndVote.apply {
                bodyAnimation.setAnimationFromUrl(BASE_URL.plus(user.userAnim))
                bodyAnimation.visibility = View.VISIBLE
                bodyAnimation.playAnimation()
            }
            // stop timer
            if (::speechTimer.isInitialized) speechTimer.cancel()
            binding.timeLeft.progress = 0f
            // launch timer
            val process = binding.timeLeft
            process.progressMax = timer.toFloat()
            speechTimer = object : CountDownTimer(timer * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    process.progress = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {
                    speechTimeUp?.let {
                        it()
                    }
                }
            }.start()

        }
    }

    suspend fun currentUserSpeechEnd(userId: String) {
        clearLastSpoken(userId = userId)
    }

    private suspend fun clearLastSpoken(userId: String) = CoroutineScope(Dispatchers.IO).launch {
        NatoFragment.inGameUsers.find {
            it.userId == userId
        }?.let {
            val layer = UserLayer.getLayerArray[it.userIndex]
            NatoFragment.userActionHistory.find { find ->
                find.userId == userId
            }?.let {
                // stop lottie anim
                withContext(Dispatchers.Main) {
                    // stop timer
                    if (::speechTimer.isInitialized) speechTimer.cancel()
                    binding.timeLeft.progress = 0f
                    // clear anim
                    layer.animSpeaking.also { anim ->
                        anim.alpha = 0f
                        anim.cancelAnimation()
                    }

                    binding.layoutDayAndVote.apply {
                        bodyAnimation.visibility = View.INVISIBLE
                        bodyAnimation.cancelAnimation()
                    }
                }
            }
        }
    }

    suspend fun mafiaSpeechEnd() {
        clearMafiaSpoken()
    }

    private suspend fun clearMafiaSpoken() = CoroutineScope(Dispatchers.IO).launch {
        for (i in NatoFragment.inGameUsers.indices) {
            val user = NatoFragment.inGameUsers[i]
            val layer = UserLayer.getLayerArray[user.userIndex]
            NatoFragment.userActionHistory.find { find ->
                find.userId == user.userId
            }?.let {
                // stop lottie anim
                withContext(Dispatchers.Main) {
                    // end timer
                    speechTimer.cancel()
                    binding.timeLeft.progress = 0f
                    layer.animSpeaking.also { anim ->
                        anim.alpha = 0f
                        anim.cancelAnimation()
                    }
                    // body anim
                    binding.layoutDayAndVote.also {
                        it.bodyAnimation.visibility = View.INVISIBLE
                        it.bodyAnimation.cancelAnimation()
                    }
                }
            }
        }

    }

    private fun likeDislikeTimer(
        layer: LayerPlayerDayBinding
    ) {
        object : CountDownTimer(2500, 500) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                layer.apply {
                    animLike.alpha = 0f
                    animDislike.alpha = 0f
                }
            }

        }.start()

    }

    suspend fun clearUsersSpeaking() {
        NatoFragment.userActionHistory.asFlow()
            .filter { !it.userStatus.isTalking }
            .collect{
                clearLastSpoken(userId = it.userId)
            }
    }

    fun moderatorConnectionStatus(
        status: InGameModeratorStatus.InGameModeratorStatusData,
        moderator: InGameUsersDataEntity.InGameUserData,
        fromChaos: Boolean
    ) {
        if (!fromChaos) {
            binding.layoutDayAndVote.playerEleven.apply {
                animSpeaking.also {
                    it.alpha = 0f
                    it.cancelAnimation()
                }
                imgUser.alpha = 1f
                if (status.connected) {
                    imgUser.load(R.drawable.round_wifi_off_24)
                } else {
                    imgUser.load(BASE_URL.plus(moderator.userImage))
                }
            }
        } else {
            binding.layerChaos.layerModerator.apply {
                animSpeaking.also {
                    it.alpha = 0f
                    it.cancelAnimation()
                }
                imgUser.alpha = 1f
                if (status.connected) {
                    imgUser.load(R.drawable.round_wifi_off_24)
                } else {
                    imgUser.load(BASE_URL.plus(moderator.userImage))
                }
            }
        }
    }

    private var speechTimeUp: (() -> Unit)? = null
    fun speechTimeUpCallback(it: () -> Unit) {
        speechTimeUp = it
    }
}