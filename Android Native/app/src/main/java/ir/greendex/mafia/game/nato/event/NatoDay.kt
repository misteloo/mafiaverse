package ir.greendex.mafia.game.nato.event

import android.os.CountDownTimer
import androidx.fragment.app.FragmentManager
import ir.greendex.mafia.databinding.FragmentNatoBinding
import ir.greendex.mafia.entity.game.general.CurrentSpeechEntity
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.UsersChallengeStatusEntity
import ir.greendex.mafia.game.general.action_anim.InGameAnimHandler
import ir.greendex.mafia.game.general.day_part.Challenge
import ir.greendex.mafia.game.general.day_part.UserStatus
import ir.greendex.mafia.game.general.day_part.Vote
import ir.greendex.mafia.game.general.day_part.nato.TargetCoverAbout
import ir.greendex.mafia.game.nato.NatoFragment
import ir.greendex.mafia.game.nato.listeners.NatoDayActionListener
import ir.greendex.mafia.util.LIKE_DISLIKE_FOR_USER_INTERVAL
import ir.greendex.mafia.util.extension.disableDoubleClick
import javax.inject.Inject

class NatoDay @Inject constructor(
    private val userStatus: UserStatus,
    private val challenge: Challenge,
    private val vote: Vote,
    private val targetCovert: TargetCoverAbout
) {

    private lateinit var actionListener: NatoDayActionListener
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: FragmentNatoBinding
    private lateinit var inGameAnimHandler: InGameAnimHandler
    private var mySpeechTurn = false
    private var currentPlayerSpeaking = false


    fun initBinding(
        binding: FragmentNatoBinding, fragmentManager: FragmentManager,
        inGameAnimHandler: InGameAnimHandler
    ) {
        this.binding = binding
        this.fragmentManager = fragmentManager
        this.inGameAnimHandler = inGameAnimHandler

        // create cls
        onCreate()

        // action
        userStatus.initBinding(binding = binding)
        // challenge
        challenge.initBinding(binding = binding)
        // vote
        vote.initBinding(binding = binding)
        // target cover
        targetCovert.initBinding(binding = binding)
    }

    fun setListener(actionListener: NatoDayActionListener) {
        this.actionListener = actionListener
    }

    private fun onCreate() {
        binding.layerAnimAction.apply {
            // like callback
            fabLike.setOnClickListener {
                // callback
                actionListener.onLike()
                // timer
                likeDislikeTimer()
            }
            // dislike callback
            fabDislike.setOnClickListener {
                // callback
                actionListener.onDislike()
                // timer
                likeDislikeTimer()
            }
            // challenge req callback
            fabChallengeRequest.setOnClickListener {
                fabChallengeRequest.isEnabled = false
                // callback
                actionListener.onChallengeReq()
                // timer
                challenge.startChallengeReqTimer()
            }
        }
        // next player
        binding.layerAnimSpeech.apply {
            fabNextPlayer.setOnClickListener {
                fabNextPlayer.disableDoubleClick(1)
                mySpeechTurn = false
                currentPlayerSpeaking = false
                // next player callback
                actionListener.onNextPlayer()
            }
        }
        // vote to player
        binding.layerAnimVote.apply {
            fabConfirmVote.setOnClickListener {
                fabConfirmVote.disableDoubleClick(1)
                actionListener.onSubmitVote()
            }
        }

        // speech time up callback
        userStatus.speechTimeUpCallback {
            if (mySpeechTurn) mySpeechTurn = false
        }


        // callback accept challenge
        challenge.onAcceptChallengeCallback {
            actionListener.onChallengeAccepted(userId = it)
        }
        // callback on accept target cover
        targetCovert.onAcceptHandRiseCallback {
            actionListener.onSubmitHandRiseToTargetCover(userId = it)
        }

    }

    suspend fun getCurrentSpeech(currentUser: CurrentSpeechEntity) {
        userStatus.getCurrentSpeech(currentUser = currentUser)
//        speech.getCurrentSpeech(currentUser = currentUser)
        mySpeechTurn = currentUser.currentUserId == NatoFragment.myUserId
        challenge.setChallengeAcceptable(acceptable = currentUser.currentUserId == NatoFragment.myUserId)
    }

    suspend fun currentSpeechEnd(userId: String) {
        userStatus.currentUserSpeechEnd(userId = userId)
    }

    suspend fun mafiaSpeechEnd(){
        userStatus.mafiaSpeechEnd()
    }

    suspend fun updateGameAction(data: List<GameActionEntity.GameActionData>) {
        // user status
        userStatus.getUserData(usersData = data)
        // challenge
        challenge.getUserChallenges(challenge = data)
        // user status
        vote.userHandRise(users = data)
        /*// target cover
        targetCovert.userActionHistory(usersData = data)*/
    }

    suspend fun updateSingleGameAction(item:GameActionEntity.GameActionData){
        userStatus.updateSingleActionUi(item = item)
    }

    private fun likeDislikeTimer() {
        binding.layerAnimAction.apply {
            fabLike.isEnabled = false
            fabDislike.isEnabled = false
            object : CountDownTimer(LIKE_DISLIKE_FOR_USER_INTERVAL, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = (millisUntilFinished / 1000L).toFloat()
                    progressLike.progress = progress
                    progressDislike.progress = progress
                }

                override fun onFinish() {
                    progressLike.progress = 0f
                    progressDislike.progress = 0f
                    fabLike.isEnabled = true
                    fabDislike.isEnabled = true
                }

            }.start()
        }
    }
    suspend fun clearUsersSpeaking(){
        userStatus.clearUsersSpeaking()
    }
    fun onAssetsClick() {
        // challenge
        challenge.onClickLayerArray()
        // target cover
        targetCovert.onClickLayerArray()
    }

    fun challengeStatus(challengeList: List<UsersChallengeStatusEntity.UserChallengeStatusData>) {
        challenge.setChallengeStatus(challengeList = challengeList)
    }

    fun setMyTurnSpeech(mySpeechTurn: Boolean) {
        this.mySpeechTurn = mySpeechTurn
    }

    fun setWhichUserRequestTargetCover(userId: String) {
        targetCovert.setWhichUserRequest(whichUserRequest = userId)
    }

}

