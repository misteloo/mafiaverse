package ir.greendex.mafia.game.nato.event

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import coil.load
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.databinding.LayerPlayerDayBinding
import ir.greendex.mafia.entity.game.general.ChaosUserSpeechEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteResultEntity
import ir.greendex.mafia.entity.game.general.CurrentSpeechEntity
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorStatus
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.general.enum_cls.UserJoinTypeEnum
import ir.greendex.mafia.game.general.day_part.UserStatus
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.listeners.NatoChaosListener
import ir.greendex.mafia.util.BASE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NatoChaos @Inject constructor(
    private val userStatus: UserStatus
) {
    private lateinit var binding: FragmentNatoBinding
    private lateinit var listener: NatoChaosListener
    private lateinit var speechTimer: CountDownTimer
    private val layerArray by lazy { mutableListOf<LayerPlayerDayBinding>() }
    private val aliveUsers by lazy { mutableListOf<String>() }
    private var isModerator: Boolean = false
    private var startChaos = false
    private var lastSpoken: String? = null


    fun initBinding(binding: FragmentNatoBinding) {
        this.binding = binding
        // bind status
        userStatus.initBinding(binding = binding)
    }

    private fun onCreate(isModerator: Boolean) = CoroutineScope(Dispatchers.Main).launch {
        this@NatoChaos.binding.apply {
            if (!isModerator) {
                layerArray.clear()
                layerArray.add(layerChaos.layerPlayerOne)
                layerArray.add(layerChaos.layerPlayerTwo)
                layerArray.add(layerChaos.layerPlayerThree)

                // on click listeners
                binding.layerChaos.apply {
                    this.btnVotePlayerOne.setOnClickListener {
                        // callback
                        listener.onVoteToSpecificUser(userId = aliveUsers[0])
                        disableVote(userJoinType = UserJoinTypeEnum.PLAYER)
                    }

                    this.btnVotePlayerTwo.setOnClickListener {
                        // callback
                        listener.onVoteToSpecificUser(userId = aliveUsers[1])
                        disableVote(userJoinType = UserJoinTypeEnum.PLAYER)

                    }

                    this.btnVotePlayerThree.setOnClickListener {
                        // callback
                        listener.onVoteToSpecificUser(userId = aliveUsers[2])
                        disableVote(userJoinType = UserJoinTypeEnum.PLAYER)

                    }
                }
            }
        }
    }

    fun setListener(listener: NatoChaosListener) {
        this.listener = listener
    }

    suspend fun startChaos(data: List<GameActionEntity.GameActionData>, isModerator: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            aliveUsers.clear()
            startChaos = true
            this@NatoChaos.isModerator = isModerator
            // setup basic layer
            val job = onCreate(isModerator)
            job.join()
            // bind user to ui
            for (index in data.indices) {
                val dataItem = data[index]
                // store alive users game id
                aliveUsers.add(dataItem.userId)

                NatoFragment.inGameUsers.find {
                    it.userId == dataItem.userId
                }?.let { let ->
                    layerArray[index].also { layer ->
                        NatoFragment.userActionHistory.find {
                            it.userId == let.userId
                        }?.apply {
                            NatoFragment.inGameUsers.find {
                                it.userId == this.userId
                            }?.also { user ->
                                withContext(Dispatchers.Main) {
                                    updateActionUi(layer = layer, status = this@apply, user = user)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun getCurrentSpeech(currentUser: CurrentSpeechEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val job = clearLastSpoken()
            job.join()
            NatoFragment.inGameUsers.find {
                it.userId == currentUser.currentUserId
            }?.apply {
                val index = aliveUsers.indexOf(this.userId)
                val layer = layerArray[index]
                withContext(Dispatchers.Main) {
                    updateSpeechUi(user = this@apply, layer = layer, currentUser.timer)
                    // current user speech
                    lastSpoken = currentUser.currentUserId
                }
            }
        }
    }

    private fun updateActionUi(
        layer: LayerPlayerDayBinding,
        status: GameActionEntity.GameActionData,
        user: InGameUsersDataEntity.InGameUserData
    ) {
        layer.apply {
            txtUsername.text = user.userName
            if (!status.userStatus.isAlive) {
                imgUser.load(R.drawable.image_rip)
                return
            }
            if (!status.userStatus.isConnected) {
                imgUser.load(R.drawable.round_wifi_off_24)
                return
            }

            imgUser.load(BASE_URL.plus(user.userImage))

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

            val process = binding.timeLeft
            process.progressMax = timer.toFloat()
            speechTimer = object : CountDownTimer(timer * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    process.progress = (millisUntilFinished / 1000L).toFloat()
                }

                override fun onFinish() {}
            }.start()

        }
    }

    suspend fun currentPlayerSpeechEnd() {
        clearLastSpoken()
    }


    suspend fun updateUserData(usersData: List<GameActionEntity.GameActionData>) {
        if (!startChaos) return
        // update
        usersData.filter {
            it.userStatus.isAlive
        }.forEach {
            val userIndex = aliveUsers.indexOf(it.userId)
            val layer = layerArray[userIndex]
            withContext(Dispatchers.Main) {
                NatoFragment.inGameUsers.find { find ->
                    find.userId == it.userId
                }?.apply {
                    updateActionUi(layer = layer, status = it, user = this)
                }
            }
        }
    }

    private suspend fun clearLastSpoken() = CoroutineScope(Dispatchers.IO).launch {
        if (lastSpoken == null) return@launch
        val layer = layerArray[aliveUsers.indexOf(lastSpoken)]
        NatoFragment.userActionHistory.find { find ->
            find.userId == lastSpoken
        }?.let {
            // stop lottie anim
            withContext(Dispatchers.Main) {
                layer.animSpeaking.also { anim ->
                    anim.alpha = 0f
                    anim.cancelAnimation()
                }
                speechTimer.cancel()
                binding.timeLeft.progress = 0f
            }
            lastSpoken = null
        }
    }

    fun startVote(data: ChaosVoteEntity.ChaosVoteData) {
        binding.layerChaos.apply {
            data.availableUsers.forEach { user ->
                when (aliveUsers.indexOf(user)) {
                    0 -> btnVotePlayerOne.also {
                        it.alpha = 1f
                        it.isClickable = true
                    }

                    1 -> btnVotePlayerTwo.also {
                        it.alpha = 1f
                        it.isClickable = true
                    }

                    2 -> btnVotePlayerThree.also {
                        it.alpha = 1f
                        it.isClickable = true
                    }
                }
            }
        }
    }

    fun voteResult(result: ChaosVoteResultEntity.ChaosVoteResultData) {
        NatoFragment.inGameUsers.find {
            it.userId == result.toUser
        }?.let { user ->
            binding.layerChaos.apply {
                when (aliveUsers.indexOf(result.fromUser)) {
                    0 -> {
                        layerHandShakePlayerOne.alpha = 1f
                        imgOnVotePlayerOne.also {
                            it.root.visibility = View.VISIBLE
                            it.imgUser.load(BASE_URL.plus(user.userImage))
                            it.txtUsername.text = user.userName
                        }
                    }

                    1 -> {
                        layerHandShakePlayerTwo.alpha = 1f
                        imgOnVotePlayerTwo.also {
                            it.root.visibility = View.VISIBLE
                            it.imgUser.load(BASE_URL.plus(user.userImage))
                            it.txtUsername.text = user.userName
                        }
                    }

                    2 -> {
                        layerHandShakePlayerThree.alpha = 1f
                        imgOnVotePlayerThree.also {
                            it.root.visibility = View.VISIBLE
                            it.imgUser.load(BASE_URL.plus(user.userImage))
                            it.txtUsername.text = user.userName
                        }
                    }
                }
            }
        }
    }

    fun disableVote(userJoinType: UserJoinTypeEnum) {
        if (userJoinType == UserJoinTypeEnum.PLAYER) {
            binding.layerChaos.apply {
                btnVotePlayerOne.also {
                    it.alpha = 0f
                    it.isClickable = false
                }
                btnVotePlayerTwo.also {
                    it.alpha = 0f
                    it.isClickable = false
                }
                btnVotePlayerThree.also {
                    it.alpha = 0f
                    it.isClickable = false
                }
            }
        }
    }

    fun hideVoteResult() {
        binding.layerChaos.apply {
            layerHandShakePlayerOne.alpha = 0f
            imgOnVotePlayerOne.also {
                it.root.visibility = View.INVISIBLE
                it.imgUser.load(null)
                it.txtUsername.text = ""
            }
            layerHandShakePlayerTwo.alpha = 0f
            imgOnVotePlayerTwo.also {
                it.root.visibility = View.INVISIBLE
                it.imgUser.load(null)
                it.txtUsername.text = ""

            }
            layerHandShakePlayerThree.alpha = 0f
            imgOnVotePlayerThree.also {
                it.root.visibility = View.INVISIBLE
                it.imgUser.load(null)
                it.txtUsername.text = ""
            }
        }
    }

    fun allSpeechData(users: List<ChaosUserSpeechEntity.ChaosUserSpeechData>) {
        Log.i("LOG", "alive users: $aliveUsers")
        Log.i("LOG", "talking users: $users")
        users.forEach { user ->
            aliveUsers.indexOfFirst {
                it == user.userId
            }.also { index ->
                if (index > 0){
                    val layer = layerArray[index]
                    if (user.talking) {
                        layer.animSpeaking.also {
                            it.alpha = 1f
                            it.playAnimation()
                        }
                    } else {
                        layer.animSpeaking.also {
                            it.alpha = 0f
                            it.cancelAnimation()
                        }
                    }
                }
            }
        }
    }

    fun endAllSpeech() {
        aliveUsers.forEach { alive ->
            val layer = layerArray[aliveUsers.indexOf(alive)]
            layer.animSpeaking.also {
                it.alpha = 0f
                it.cancelAnimation()
            }
            // hide vote result
            hideVoteResult()
        }
    }

    fun moderatorData(data: InGameUsersDataEntity.InGameUserData) {
        binding.layerChaos.layerModerator.apply {
            imgUser.load(BASE_URL.plus(data.userImage))
            txtUsername.text = data.userName
            txtPlayerCharacter.visibility = View.VISIBLE
            txtPlayerCharacter.text = "بازی گردان"
        }
    }

    fun moderatorStatus(status: InGameModeratorStatus.InGameModeratorStatusData) {
        if (!status.talking) userStatus.moderatorConnectionStatus(
            status = status, moderator = NatoFragment.moderatorData!!, fromChaos = true
        )
//        else speech.moderatorSpeech(fromChaos = true)
    }

    fun onTurnToShake(userId:String?){
        binding.layerChaos.apply {
            if (userId == null){
                linearTurnShake.visibility = View.INVISIBLE
                layerPlayerShakingTurn.also {
                    it.imgUser.load(null)
                    it.txtUsername.text = ""
                }
            }else {
                NatoFragment.inGameUsers.find {
                    it.userId == userId
                }?.apply {
                    linearTurnShake.visibility = View.VISIBLE
                    layerPlayerShakingTurn.also {
                        it.imgUser.load(BASE_URL.plus(this.userImage))
                        it.txtUsername.text = this.userName
                    }
                }
            }
        }
    }

}