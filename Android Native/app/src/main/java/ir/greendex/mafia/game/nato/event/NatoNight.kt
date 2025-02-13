package ir.greendex.mafia.game.nato.event

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.databinding.LayerNatoAnimNightAbilityBinding
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.nato.NatoCharacters
import ir.greendex.mafia.entity.game.nato.NatoInGameNightUsers
import ir.greendex.mafia.game.adapter.nato.NatoNightActionAdapter
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.NatoGuessCharacterBsFragment
import ir.greendex.mafia.game.nato.NatoRiffleManBsFragment
import ir.greendex.mafia.game.nato.listeners.NatoNightListener
import ir.greendex.mafia.util.extension.bindToClosingSheet
import javax.inject.Inject

class NatoNight @Inject constructor(
    private val context: Context,
    private val nightActionAdapter: NatoNightActionAdapter
) {
    private lateinit var binding: FragmentNatoBinding
    private lateinit var nightActionBinding: LayerNatoAnimNightAbilityBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var listener: NatoNightListener
    private lateinit var character: NatoCharacters
    private lateinit var actTimer: CountDownTimer
    private var mafiaShot: Boolean = false
    private var myTurnToNightAct = false
    private var maxCount = 0
    private val availableUser by lazy { mutableListOf<InGameUsersDataEntity.InGameUserData>() }
    private val selectedUsersToNightAction by lazy { mutableListOf<NatoInGameNightUsers>() }
    private var timeLeftNightAct = 0L
    private var riffleManFighterGun = 1
    private var nightGuessCharacterBs: BottomSheetDialogFragment? = null
    private var nightRiffleManBs: BottomSheetDialogFragment? = null
    fun initBinding(binding: FragmentNatoBinding, fragmentManager: FragmentManager) {
        this.binding = binding
        this.fragmentManager = fragmentManager
    }


    fun setListener(listener: NatoNightListener) {
        this.listener = listener
    }

    fun initNightActionLayer(nightActionBinding: LayerNatoAnimNightAbilityBinding) {
        this.nightActionBinding = nightActionBinding
        // create
        onCreate()
    }

    private fun onCreate() {
        // rv
        nightActionBinding.apply {
            rvUsers.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvUsers.adapter = nightActionAdapter
        }

        // user size in adapter callback
        nightActionAdapter.usersInListCallback {
            maxCount -= it
            bindMaxCount(maxCount = this.maxCount)
            nightActionBinding.fabAction.isEnabled = it > 0
        }

        // remove user from night act adapter
        nightActionAdapter.onUserDeleteCallback {
            maxCount++
            bindMaxCount(maxCount = this.maxCount)
            selectedUsersToNightAction.remove(it)
            // bind to recycler
            nightActionAdapter.modifierUsers(newUser = selectedUsersToNightAction)
        }

        // act on selected users at night
        nightActionBinding.apply {
            fabAction.setOnClickListener {
                listener.onNightActResult(users = selectedUsersToNightAction, mafiaShot = mafiaShot)
                stopTimer(stopWithClick = true)
                nightActionAdapter.modifierUsers(newUser = mutableListOf())
            }
        }
    }

    fun getUserIdOnNightActing(userId: String) {
        NatoFragment.inGameUsers.find { find -> find.userId == userId }
            ?.let { let ->
                if (maxCount > 0) {
                    if (availableUser.contains(let)) {
                        if (selectedUsersToNightAction.find { find -> find.userId == let.userId } == null) {
                            when (character) {
                                NatoCharacters.RIFLEMAN -> riffleManNightAct(
                                    selectedUser = let
                                )

                                NatoCharacters.NATO -> {
                                    if (mafiaShot) {
                                        // add to list
                                        addUserToNightAct(
                                            userName = let.userName,
                                            userId = let.userId,
                                            userImage = let.userImage,
                                            natoAct = false
                                        )
                                    } else natoNightAct(selectedUser = let)
                                }

                                else -> {
                                    // add to list
                                    addUserToNightAct(
                                        userName = let.userName,
                                        userId = let.userId,
                                        userImage = let.userImage,
                                        natoAct = false
                                    )
                                }
                            }
                        } else msg("این بازیکن قبلا انتخاب شده")
                    } else msg("این بازیکن مجاز نیست")
                } else msg("نمیتونی بیشتر از حد مجاز انتخاب کنی")
            }
    }

    private fun riffleManNightAct(selectedUser: InGameUsersDataEntity.InGameUserData) {

        // bs gun kind
        nightRiffleManBs = NatoRiffleManBsFragment(
            fighterGunEnable = riffleManFighterGun > 0,
            user = selectedUser
        )
        nightRiffleManBs?.bindToClosingSheet()
        nightRiffleManBs?.isCancelable = false
        nightRiffleManBs?.show(fragmentManager, null)
        nightRiffleManBs?.let {
            (it as NatoRiffleManBsFragment).onGivenGunCallback { user, gun ->
                // if given gun being fighter reduce number of fighter gun in global
                if (gun == "fighter") riffleManFighterGun--

                listener.onNightActResult(
                    users = listOf(
                        NatoInGameNightUsers(
                            userName = user.userName,
                            userId = user.userId,
                            userImage = user.userImage,
                            hasGun = true,
                            gunKind = gun,
                            natoAct = false
                        )
                    ), mafiaShot = mafiaShot
                )
                stopTimer(stopWithClick = true)
                myTurnToNightAct = false
            }
        }
    }

    private fun natoNightAct(selectedUser: InGameUsersDataEntity.InGameUserData) {
        nightGuessCharacterBs = NatoGuessCharacterBsFragment(user = selectedUser)
        nightGuessCharacterBs?.show(fragmentManager, null)
        nightGuessCharacterBs?.bindToClosingSheet()
        nightGuessCharacterBs?.let {
            (it as NatoGuessCharacterBsFragment).onGuessCharacterCallback { user, guessCharacter, timeUp ->
                if (!timeUp) {

                    listener.onNightActResult(
                        users = listOf(
                            NatoInGameNightUsers(
                                userName = user.userName,
                                userId = user.userId,
                                userImage = user.userImage,
                                hasGun = false,
                                natoAct = true,
                                guessCharacter = guessCharacter
                            )
                        ), mafiaShot = mafiaShot
                    )
                    stopTimer(stopWithClick = true)
                    myTurnToNightAct = false
                }
            }
        }
    }

    private fun addUserToNightAct(
        userName: String,
        userId: String,
        userImage: String,
        hasGun: Boolean = false,
        gunKind: String? = null,
        natoAct: Boolean,
        guessCharacter: NatoCharacters? = null
    ) {
        // add to list
        selectedUsersToNightAction.add(
            NatoInGameNightUsers(
                userName = userName,
                userId = userId,
                userImage = userImage,
                hasGun = hasGun,
                gunKind = gunKind,
                natoAct = natoAct,
                guessCharacter = guessCharacter
            )
        )
        // bind to recycler
        nightActionAdapter.modifierUsers(newUser = selectedUsersToNightAction)
    }


    fun nightStatus(
        startNight: Boolean,
    ) {

        if (startNight) {
            for (i in NatoFragment.inGameUsers.indices) {
                val layer = UserLayer.getLayerArray[i]
                layer.apply {
                    imgNight.visibility = View.VISIBLE
                }
            }
        } else {
            for (i in NatoFragment.inGameUsers.indices) {
                val layer = UserLayer.getLayerArray[i]
                layer.apply {
                    imgNight.visibility = View.INVISIBLE
                    imgLock.visibility = View.INVISIBLE
                    imgCharacter.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun nightAct(
        users: List<String>,
        timer: Int,
        maxCount: Int,
        currentPlayerCharacter: NatoCharacters,
        mafiaShot: Boolean = false
    ) {
        timeLeftNightAct = (timer * 1000L)
        availableUser.clear()
        myTurnToNightAct = true
        this.mafiaShot = mafiaShot
        this.maxCount = maxCount
        character = currentPlayerCharacter

        for (i in users.indices) {
            val userId = users[i]
            NatoFragment.inGameUsers.find { it.userId == userId }?.let {
                availableUser.add(
                    InGameUsersDataEntity.InGameUserData(
                        userName = it.userName,
                        userId = it.userId,
                        userIndex = it.userIndex,
                        userImage = it.userImage,
                        userAnim = it.userAnim,
                    )
                )
            }
        }


        // bind ui
        bindAvailableUsersToUi(users = availableUser, timer = timer)

        // bind max count to ui
        bindMaxCount(maxCount = this.maxCount)
    }

    private fun bindMaxCount(maxCount: Int) {
        binding.layoutDayAndVote.apply {
            layerMaxCount.visibility = View.VISIBLE
            txtCount.text = maxCount.toString().plus(" حق انتخاب ")
        }
    }

    private fun bindAvailableUsersToUi(
        users: List<InGameUsersDataEntity.InGameUserData>,
        timer: Int
    ) {

        val allUsers = NatoFragment.inGameUsers
        val unAvailableUsers = allUsers.minus(users.toSet())

        unAvailableUsers.forEach {
            UserLayer.getLayerArray[it.userIndex].apply {
                imgLock.visibility = View.VISIBLE
            }
        }

        // max progress
        binding.timeLeft.progressMax = timer.toFloat()
        // stop timer
        if (::actTimer.isInitialized) actTimer.cancel()
        actTimer = object : CountDownTimer(timer * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = millisUntilFinished / 1000L
                timeLeftNightAct = time
                binding.apply {
                    timeLeft.progress = time.toFloat()
                }
            }

            override fun onFinish() {
                binding.apply {
                    stopTimer()
                    timeLeftNightAct = 0
                    // clear selected users
                    nightActionAdapter.modifierUsers(newUser = mutableListOf())
                }
            }
        }.start()
    }

    private fun stopTimer(stopWithClick: Boolean = false) {
        actTimer.cancel()

        binding.apply {
            timeLeft.progress = 0f
            // clear selected users
            selectedUsersToNightAction.clear()
            // clear available users
            availableUser.clear()
            // callback
            if (!stopWithClick) listener.onNightActTimeUp()
            // disable on click
            myTurnToNightAct = false
            // bind night ui
            nightStatus(startNight = true)
        }
    }

    private fun msg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}