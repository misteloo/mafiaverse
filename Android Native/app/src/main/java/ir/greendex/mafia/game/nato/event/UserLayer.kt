package ir.greendex.mafia.game.nato.event

import android.os.CountDownTimer
import android.view.View
import coil.load
import coil.transform.CircleCropTransformation
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.databinding.LayerPlayerDayBinding
import ir.greendex.mafia.entity.game.general.InGameModeratorStatus
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.general.UsersCharacterEntity
import ir.greendex.mafia.entity.game.general.enum_cls.UserJoinTypeEnum
import ir.greendex.mafia.game.general.day_part.UserStatus
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.listeners.NatoDayActionListener
import ir.greendex.mafia.game.nato.listeners.NatoUserLayerListener
import ir.greendex.mafia.util.BASE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserLayer @Inject constructor(
    private val userStatus: UserStatus
) {
    private var playerViewLayer = arrayListOf<LayerPlayerDayBinding>()
    private lateinit var binding: FragmentNatoBinding
    private lateinit var userLayerListener: NatoUserLayerListener
    private lateinit var dayGunTimer: CountDownTimer
    private lateinit var userActionListener: NatoDayActionListener

    companion object {
        val getLayerArray get() = layerArray

        private val layerArray = mutableListOf<LayerPlayerDayBinding>()
    }

    fun clearLayerArray() {
        layerArray.clear()
    }

    fun binding(
        binding: FragmentNatoBinding,
        userLayerListener: NatoUserLayerListener,
        userActionListener: NatoDayActionListener
    ) {
        this.binding = binding
        this.userLayerListener = userLayerListener
        this.userActionListener = userActionListener
        binding.apply {
            playerViewLayer.clear()
            playerViewLayer.add(layoutDayAndVote.playerOne)
            playerViewLayer.add(layoutDayAndVote.playerTwo)
            playerViewLayer.add(layoutDayAndVote.playerThree)
            playerViewLayer.add(layoutDayAndVote.playerFour)
            playerViewLayer.add(layoutDayAndVote.playerFive)
            playerViewLayer.add(layoutDayAndVote.playerSix)
            playerViewLayer.add(layoutDayAndVote.playerSeven)
            playerViewLayer.add(layoutDayAndVote.playerEight)
            playerViewLayer.add(layoutDayAndVote.playerNine)
            playerViewLayer.add(layoutDayAndVote.playerTen)
        }
    }

    suspend fun setUsers(
        joinTypeEnum: UserJoinTypeEnum, users: List<InGameUsersDataEntity.InGameUserData>,
        callback:(()->Unit)
    ) {
        /*if (joinTypeEnum == UserJoinTypeEnum.MODERATOR) {
            binding.apply {
                layerArray.add(layerModerator.playerOne)
                layerArray.add(layerModerator.playerTwo)
                layerArray.add(layerModerator.playerThree)
                layerArray.add(layerModerator.playerFour)
                layerArray.add(layerModerator.playerFive)
                layerArray.add(layerModerator.playerSix)
                layerArray.add(layerModerator.playerSeven)
                layerArray.add(layerModerator.playerEight)
                layerArray.add(layerModerator.playerNine)
                layerArray.add(layerModerator.playerTen)
            }
        }*/
        users.asFlow().onStart {
            clearLayerArray()
        }.onCompletion {
            // enable on click to users
            onPlayerClicked()
            onPlayerLongClicked()
            callback()
        }.collectIndexed { index, value ->
            layerArray.add(playerViewLayer[index])
            setUsersDataToUi(layer = layerArray[index], index = index, user = value)
            // set tag
            layerArray[index].root.tag = value.userId
        }
    }

    private suspend fun onPlayerClicked() {
        layerArray.asFlow().flowOn(Dispatchers.Main).collect { view ->
            view.root.setOnClickListener {
                userLayerListener.onClickedToUser(userId = it.tag.toString())
            }
        }
    }

    private suspend fun onPlayerLongClicked() {
        layerArray.asFlow().flowOn(Dispatchers.Main).collect { view ->
            view.root.setOnLongClickListener {
                userLayerListener.onClickedToUser(userId = it.tag.toString())
                return@setOnLongClickListener false
            }
        }
    }

    private fun setUsersDataToUi(
        layer: LayerPlayerDayBinding, index: Int, user: InGameUsersDataEntity.InGameUserData
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            layer.apply {
                NatoFragment.userActionHistory.find {
                    it.userId == user.userId
                }?.let {
                    if (!it.userStatus.isAlive) imgUser.load(R.drawable.image_rip)
                    else if (!it.userStatus.isConnected) imgUser.load(R.drawable.round_wifi_off_24)
                    else imgUser.load(BASE_URL.plus(user.userImage)) {
                        crossfade(true)
                        placeholder(R.drawable.mafia_icon)
                        transformations(CircleCropTransformation())
                    }
                }
                txtUsername.text = user.userName
                txtPlayerIndex.text = index.plus(1).toString()
            }
        }
    }


    fun setCharacterForModerator(it: List<UsersCharacterEntity.UsersCharacterData>) {
        // bind ui
        NatoFragment.inGameUsers.forEach { loop ->
            it.find { find ->
                find.userId == loop.userId
            }?.let { let ->
                getLayerArray[loop.userIndex].apply {
                    txtPlayerCharacter.text = let.character
                    txtPlayerCharacter.visibility = View.VISIBLE
                }
            }
        }

    }


    fun moderatorData(data: InGameUsersDataEntity.InGameUserData) {
        binding.layoutDayAndVote.playerEleven.apply {
            imgUser.load(BASE_URL.plus(data.userImage))
            txtUsername.text = data.userName
            txtPlayerCharacter.visibility = View.VISIBLE
            txtPlayerCharacter.text = "بازی گردان"
        }
    }

    fun moderatorStatus(status: InGameModeratorStatus.InGameModeratorStatusData) {
        if (!status.talking) userStatus.moderatorConnectionStatus(
            status = status, moderator = NatoFragment.moderatorData!!, fromChaos = false
        )
//        else speech.moderatorSpeech(fromChaos = false)
    }

    private var onModeratorClickToUser: ((String) -> Unit)? = null
    fun onModeratorClickedToUserCallback(it: (String) -> Unit) {
        onModeratorClickToUser = it
    }

    fun userActiveDayGun() {
        userActionListener.onDayUsingGun()
    }

    fun runGunTimer() {
        binding.apply {
            dayGunTimer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progressGun.progress = (millisUntilFinished / 1000).toFloat()
                }

                override fun onFinish() {
                    userActionListener.onDayUsingGunTimUp()
                }

            }.start()
        }
    }

    fun cancelGunTimer() {
        if (::dayGunTimer.isInitialized) {
            dayGunTimer.cancel()
            binding.apply {
                progressGun.progress = 0f
            }
        }
    }

}