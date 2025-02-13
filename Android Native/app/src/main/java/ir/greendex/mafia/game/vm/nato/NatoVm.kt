package ir.greendex.mafia.game.vm.nato

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.greendex.mafia.entity.game.general.ChaosTurnToShakeEntity
import ir.greendex.mafia.entity.game.general.ChaosUserSpeechEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteResultEntity
import ir.greendex.mafia.entity.game.general.CurrentSpeechEntity
import ir.greendex.mafia.entity.game.general.DayInquiryEntity
import ir.greendex.mafia.entity.game.general.DetectiveInquiryEntity
import ir.greendex.mafia.entity.game.general.EndGameResultEntity
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.GameEventEntity
import ir.greendex.mafia.entity.game.general.GodFatherShotEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorStatus
import ir.greendex.mafia.entity.game.general.InGameTurnSpeechEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.general.ModeratorLogEntity
import ir.greendex.mafia.entity.game.general.PlayVoiceEntity
import ir.greendex.mafia.entity.game.general.ReportEntity
import ir.greendex.mafia.entity.game.general.RequestSpeechOptionsEntity
import ir.greendex.mafia.entity.game.general.SpeechEndEntity
import ir.greendex.mafia.entity.game.general.SpeechOptionMsgEntity
import ir.greendex.mafia.entity.game.general.UsersChallengeStatusEntity
import ir.greendex.mafia.entity.game.general.UsersCharacterEntity
import ir.greendex.mafia.entity.game.general.UsingSpeechOptionsEntity
import ir.greendex.mafia.entity.game.general.VoiceRoomEntity
import ir.greendex.mafia.entity.game.general.VoteEntity
import ir.greendex.mafia.entity.game.general.WhichUserRequestSpeechOptionEntity
import ir.greendex.mafia.entity.game.general.enum_cls.NatoGameEventEnum
import ir.greendex.mafia.entity.game.nato.DayUsedGunEntity
import ir.greendex.mafia.entity.game.nato.DayUsingGunEntity
import ir.greendex.mafia.entity.game.nato.GunStatusEntity
import ir.greendex.mafia.entity.game.nato.MafiaDecisionEntity
import ir.greendex.mafia.entity.game.nato.MafiaSpeechEntity
import ir.greendex.mafia.entity.game.nato.MafiaVisitationEntity
import ir.greendex.mafia.entity.game.nato.NatoCharacters
import ir.greendex.mafia.entity.game.nato.NatoInGameNightUsers
import ir.greendex.mafia.entity.game.nato.NatoMafiaVisitationEntity
import ir.greendex.mafia.entity.game.nato.NatoUseAbilityEntity
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.game.nato.listeners.NatoSocketListener
import ir.greendex.mafia.game.nato.listeners.NatoViewModelListener
import ir.greendex.mafia.util.Encryption
import ir.greendex.mafia.util.IS_IN_GAME
import ir.greendex.mafia.util.ROUTER
import ir.greendex.mafia.util.base.BaseVm
import ir.greendex.mafia.util.socket.NatoScenarioSocketManager
import ir.greendex.mafia.util.sound.SoundManager
import ir.greendex.mafia.util.voice.KitInitiator
import ir.greendex.mafia.util.voice.VoiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class NatoVm @Inject constructor(
    private val encryption: Encryption,
    private val gson: Gson,
    @Named("io_scope") private val ioThr: CoroutineScope,
    private val soundManager: SoundManager
) : BaseVm() {

    private var micStatus = false
    private lateinit var natoGameSocket: NatoScenarioSocketManager
    private var voiceManager: VoiceManager = KitInitiator.getVoiceManager
    fun setNatoGameSocket(natoGameSocket: NatoScenarioSocketManager) {
        this.natoGameSocket = natoGameSocket
    }

    fun storeGameState(state: Boolean) = viewModelScope.launch {
        localRepository.storeData(IS_IN_GAME, state)
    }


    val micStatusLiveData = MutableLiveData(false)
    fun setMicStatus(status: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val job = async {
            KitInitiator.getVoiceManager.publishing(micStatus = status)
        }
        job.join()
        job.await()
        micStatusLiveData.postValue(status)
        micStatus = status
    }

    fun getMicStatus() = micStatus

    fun setRouter(router:Routing) = viewModelScope.launch {
        localRepository.storeData(ROUTER,router.name)
    }
    var onParticipantSpeakingLiveData = MutableLiveData<Boolean>()
    fun onParticipantSpeaking() {
        voiceManager.onParticipantSpeakingCallback {
            onParticipantSpeakingLiveData.postValue(it)
        }
    }

    fun getCharacter(it: String, callback: (characterEnum: NatoCharacters?, role: String) -> Unit) {
        when (it) {
            "nato" -> callback(NatoCharacters.NATO, "ناتو")
            "godfather" -> callback(NatoCharacters.GODFATHER, "پدرخوانده")
            "hostage_taker" -> callback(NatoCharacters.HOSTAGE_TAKER, "گروگانگیر")
            "citizen" -> callback(NatoCharacters.CITIZEN, "شهروند")
            "doctor" -> callback(NatoCharacters.DOCTOR, "دکتر")
            "rifleman" -> callback(NatoCharacters.RIFLEMAN, "تفنگدار")
            "guard" -> callback(NatoCharacters.GUARD, "نگهبان")
            "commando" -> callback(NatoCharacters.COMMANDO, "تکاور")
            "detective" -> callback(NatoCharacters.DETECTIVE, "کاراگاه")
            else -> callback(null, "بیننده")
        }
    }

    fun readyToGame() = viewModelScope.launch {
        val obj = JSONObject().apply {
            put("op", "ready_to_game")
        }
        natoGameSocket.sendData(obj = obj)

    }

    private fun setConnectToRoom(token: String) = viewModelScope.launch(Dispatchers.IO) {
        voiceManager.init(token)
    }

    fun setRoomId(token: String) = viewModelScope.launch(Dispatchers.IO) {
        // dc from current room
        disconnectRoom()
        delay(2000)
        // bind new token
        setConnectToRoom(token = token)
    }

    fun initNatoGameManager(callback: NatoViewModelListener) {

        natoGameSocket.init(object : NatoSocketListener {

            override fun onRoomId(id: String) {
                val res = gson.fromJson(id, VoiceRoomEntity::class.java)
                callback.onRoomId(token = res.token)
            }

            override fun onMafiaSpeech(it: String) {
                val res = gson.fromJson(it, MafiaSpeechEntity::class.java)
                callback.onMafiaSpeech(data = res)
            }

            override fun onMafiaSpeechEnd() {
                callback.onMafiaSpeechEnd()
            }

            override fun onUsersData(it: String) {
                ioThr.launch {
                    val res = gson.fromJson(it, InGameUsersDataEntity::class.java)
                    callback.onUsersData(users = res.data)
                }
            }

            override fun onGameEvent(event: String) {
                ioThr.launch {
                    val res = gson.fromJson(event, GameEventEntity::class.java)
                    callback.onGameEvent(
                        event = when (res.data.gameEvent) {
                            "day" -> NatoGameEventEnum.DAY
                            "action" -> NatoGameEventEnum.ACTION
                            "vote" -> NatoGameEventEnum.VOTE
                            "night" -> NatoGameEventEnum.NIGHT
                            "chaos" -> NatoGameEventEnum.CHAOS
                            "end" -> NatoGameEventEnum.END
                            "target_cover" -> NatoGameEventEnum.TARGET_COVERT_ABOUT
                            else -> NatoGameEventEnum.DAY
                        }
                    )
                }
            }

            override fun onUserAction(it: String) {
                val res = gson.fromJson(it, GameActionEntity::class.java)
                callback.onGameAction(data = res.data)
            }

            override fun onSpeechTurn(it: String) {
                val res = gson.fromJson(it, InGameTurnSpeechEntity::class.java)
                callback.onInGameTurnSpeechQueue(it = res)
            }

            override fun onStartSpeech() {
                callback.onStartSpeech()
            }

            override fun onSpeechTimeUp(it:String) {
                val res = gson.fromJson(it,SpeechEndEntity::class.java)
                callback.onSpeechTimeUp(data = res.data)
            }

            override fun onVoteToPlayer(it: String) {
                val res = gson.fromJson(it, VoteEntity::class.java)
                callback.onVoteToPlayer(data = res.data)
            }

            override fun onReport(it: String) {
                val res = gson.fromJson(it, ReportEntity::class.java)
                callback.onReport(data = res.data)
            }

            override fun onAcceptChallenge() {
                callback.onChallengeAccepted()
            }

            override fun onMafiaVisitation(it: String) {
                ioThr.launch {
                    val res = gson.fromJson(it, MafiaVisitationEntity::class.java)
                    val dec = encryption.encryptDecrypt(res.data.mafia)
                    val array = JSONArray(dec)
                    val mafiaList = mutableListOf<NatoMafiaVisitationEntity>()
                    val job = viewModelScope.launch {
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val index = obj.getInt("index")
                            val role = obj.getString("role")
                            val userId = obj.getString("user_id")
                            val natoCharacter = when (role) {
                                "nato" -> NatoCharacters.NATO
                                "godfather" -> NatoCharacters.GODFATHER
                                "hostage_taker" -> NatoCharacters.HOSTAGE_TAKER
                                else -> NatoCharacters.CITIZEN
                            }
                            mafiaList.add(
                                NatoMafiaVisitationEntity(
                                    index = index, role = natoCharacter, userId = userId
                                )
                            )

                        }
                    }
                    job.join()
                    callback.onMafiaVisitation(mafiaList = mafiaList)
                }
            }

            override fun onUseAbility(it: String) {
                val res = gson.fromJson(it, NatoUseAbilityEntity::class.java)
                callback.onUseAbility(it = res.data)
            }

            override fun onMafiaShot(it: String) {
                val res = gson.fromJson(it, GodFatherShotEntity::class.java)
                callback.onMafiaShot(it = res)
            }

            override fun onCurrentSpeech(it: String) {
                val res = gson.fromJson(it, CurrentSpeechEntity::class.java)
                callback.onCurrentSpeech(
                    CurrentSpeechEntity(
                        currentUserId = res.currentUserId, timer = res.timer, hasNext = res.hasNext
                    )
                )
            }

            override fun onCurrentSpeechEnd(it:String) {
                val res = gson.fromJson(it,SpeechEndEntity::class.java)
                callback.onCurrentSpeechEnd(data = res.data)
            }

            override fun onMafiaDecision(it: String) {
                val res = gson.fromJson(it, MafiaDecisionEntity::class.java)
                callback.onMafiaDecision(data = res)
            }

            override fun onDetectiveInquiry(it: String) {
                val res = gson.fromJson(it, DetectiveInquiryEntity::class.java)
                callback.onDetectiveInquiry(res)
            }

            override fun onUsersChallengeStatus(it: String) {
                val res = gson.fromJson(it, UsersChallengeStatusEntity::class.java)
                callback.onUsersChallengeStatus(userChallengeStatus = res.data)
            }

            override fun onUsingSpeechOptions(it: String) {
                val res = gson.fromJson(it, UsingSpeechOptionsEntity::class.java)
                callback.onSpeechOptions(speechOption = res.data)
            }

            override fun onWhichUserRequestSpeechOptions(it: String) {
                val res = gson.fromJson(it, WhichUserRequestSpeechOptionEntity::class.java)
                callback.onWhichUserRequestSpeechOption(which = res.data)
            }

            override fun onSpeechOptionMsg(it: String) {
                val res = gson.fromJson(it, SpeechOptionMsgEntity::class.java)
                callback.onSpeechOptionMsg(msg = res.data)
            }

            override fun onRequestSpeechOptions(it: String) {
                val res = gson.fromJson(it, RequestSpeechOptionsEntity::class.java)
                callback.onRequestSpeechOption(data = res.data)
            }

            override fun onReportGun() {
                callback.onReportGun()
            }

            override fun onGunStatus(it: String) {
                val res = gson.fromJson(it, GunStatusEntity::class.java)
                callback.onGunStatus(data = res.data)
            }

            override fun onDayUsingGun(it: String) {
                val res = gson.fromJson(it, DayUsingGunEntity::class.java)
                callback.onDayUsingGun(userId = res.data.userId)
            }

            override fun onDayUsedGun(it: String) {
                val res = gson.fromJson(it, DayUsedGunEntity::class.java)
                callback.onDayUsedGun(data = res.data)
            }

            override fun onDayInquiry(it: String) {
                val res = gson.fromJson(it, DayInquiryEntity::class.java)
                callback.onDayInquiry(data = res.data)
            }

            override fun onDayInquiryResult(it: String) {
                val res = gson.fromJson(it, DayInquiryEntity::class.java)
                callback.onDayInquiryResult(data = res.data)
            }

            override fun onChaosVoteTimeNotification() {
                callback.onChaosVoteTimeNotification()
            }

            override fun onChaosVote(it: String) {
                val res = gson.fromJson(it, ChaosVoteEntity::class.java)
                callback.onChaosVote(data = res.data)
            }

            override fun onChaosVoteResult(it: String) {
                val res = gson.fromJson(it, ChaosVoteResultEntity::class.java)
                callback.onChaosVoteResult(data = res)
            }

            override fun onChaosUserSpeech(it: String) {
                val res = gson.fromJson(it, ChaosUserSpeechEntity::class.java)
                callback.onChaosUserSpeech(data = res.data)
            }

            override fun onChaosAllSpeech() {
                callback.onChaosAllSpeech()
            }

            override fun onChaosAllSpeechEnd() {
                callback.onChaosAllSpeechEnd()
            }

            override fun onChaosTurnToShake(it: String) {
                val res = gson.fromJson(it,ChaosTurnToShakeEntity::class.java)
                callback.onChaosTurnToShake(data = res.data)
            }

            override fun onUsersCharacters(it: String) {
                val res = gson.fromJson(it, UsersCharacterEntity::class.java)
                callback.onUsersCharacters(data = res.data)
            }

            override fun onModeratorData(it: String) {
                val res = gson.fromJson(it, InGameModeratorEntity::class.java)
                if (res.data != null) {
                    callback.onModeratorData(data = res)
                }
            }

            override fun onModeratorStatus(it: String) {
                val res = gson.fromJson(it, InGameModeratorStatus::class.java)
                callback.onModeratorStatus(data = res.data)
            }

            override fun onModeratorPanelEvent(it: String) {
                val res = gson.fromJson(it, ModeratorLogEntity::class.java)
                callback.onModeratorPanelEvent(data = res.data)
            }

            override fun onEndGameResult(it: String) {
                val res = gson.fromJson(it, EndGameResultEntity::class.java)
                callback.onEndGameResult(data = res.data)
            }

            override fun onLastDecision(it: String) {
                val res = gson.fromJson(it, ChaosVoteEntity::class.java)
                callback.onLastDecision(data = res)
            }

            override fun onPlayerShowCharacter() {
                callback.onPlayerShowCharacter()
            }

            override fun onAbandon() {
                callback.onAbandon()
            }

            override fun onActionEnd() {
                callback.onActionEnd()
            }

            override fun onPlayVoice(it: String) {
                val res = gson.fromJson(it,PlayVoiceEntity::class.java)
                viewModelScope.launch {
                    soundManager.voiceCommand(id = res.data.id)
                }
            }

        })
    }

    fun setGameAction(like: Boolean = false, dislike: Boolean = false, challenge: Boolean = false) =
        ioThr.launch {
            val data = JSONObject().apply {
                if (challenge) put("action", "challenge_request")
                if (like) put("action", "like")
                if (dislike) put("action", "dislike")
            }
            val op = JSONObject().apply {
                put("op", "user_action")
                put("data", data)
            }
            natoGameSocket.sendData(obj = op)
        }

    fun acceptChallenge(userId: String) {
        val data = JSONObject().apply {
            put("user_id", userId)
        }
        val op = JSONObject().apply {
            put("op", "accept_challenge")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun nextSpeech() {
        val op = JSONObject().apply {
            put("op", "next_speech")
        }
        natoGameSocket.sendData(obj = op)
    }

    fun voteToPlayer() {
        val op = JSONObject().apply {
            put("op", "vote")
        }
        natoGameSocket.sendData(obj = op)
    }

    fun sendNightActToServer(users: List<NatoInGameNightUsers>, role: String, mafiaShot: Boolean) {
        val arr = JSONArray().apply {
            users.forEach {
                val obj = JSONObject().apply {
                    put("user_id", it.userId)
                    if (it.natoAct) put("act", it.guessCharacter!!.name.lowercase()) else put(
                        "act",
                        it.gunKind
                    )
                }
                put(obj)
            }
        }
        val data = JSONObject().apply {
            put("users", arr)
            put("role", role)
        }
        val op = JSONObject().apply {
            if (mafiaShot) put("op", "mafia_shot") else put("op", "night_act")
            put("data", data)
        }

        natoGameSocket.sendData(obj = op)
    }

    fun sendMafiaDecision(shot: Boolean, nato: Boolean, role: NatoCharacters) {
        val data = JSONObject().apply {
            put("shot", shot)
            put("nato", nato)
            put("role", role.name.lowercase())
        }
        val op = JSONObject().apply {
            put("op", "mafia_decision")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun usingSpeechOption(usingOption: Boolean) {
        val data = JSONObject().apply {
            put("using_option", usingOption)
        }
        val op = JSONObject().apply {
            put("op", "using_speech_options")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun selectVolunteerToTargetCover(userId: String) {
        val data = JSONObject().apply {
            put("user_id", userId)
        }
        val op = JSONObject().apply {
            put("op", "volunteer")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun dayUsingGun(myUserId: String) {
        val data = JSONObject().apply {
            put("user_id", myUserId)
        }
        val op = JSONObject().apply {
            put("op", "day_using_gun")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun rifleGunShot(userId: String) {
        val data = JSONObject().apply {
            put("user_id", userId)
        }
        val op = JSONObject().apply {
            put("op", "rifle_gun_shot")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun chaosUserVoteToUser(userId: String) {
        val data = JSONObject().apply {
            put("user_id", userId)
        }
        val op = JSONObject().apply {
            put("op", "chaos_vote")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun setChaosUserSpeech(userId: String, talking: Boolean) {
        val data = JSONObject().apply {
            put("user_id", userId)
            put("talking", talking)
        }
        val op = JSONObject().apply {
            put("op", "chaos_user_speech")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun setModeratorSpeaking(userId: String, speaking: Boolean) {
        val data = JSONObject().apply {
            put("user_id", userId)
            put("speaking", speaking)
        }
        val op = JSONObject().apply {
            put("op", "mod_speaking")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun moderatorForceKick(userId: String) {
        val data = JSONObject().apply {
            put("user_id", userId)
        }
        val op = JSONObject().apply {
            put("op", "mod_kick")
            put("data", data)
        }
        natoGameSocket.sendData(obj = op)
    }

    fun userEndGame() {
        natoGameSocket.userEndGame()
    }

    fun disconnectRoom() = viewModelScope.launch(Dispatchers.IO){
        voiceManager.disconnect()
    }

    fun turnOffGameSocket() = viewModelScope.launch(Dispatchers.IO) {
        natoGameSocket.turnOffGameSocket()
    }
}