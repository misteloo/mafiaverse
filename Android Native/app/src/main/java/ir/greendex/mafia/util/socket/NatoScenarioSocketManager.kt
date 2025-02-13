package ir.greendex.mafia.util.socket

import android.util.Log
import io.socket.client.Socket
import ir.greendex.mafia.game.nato.listeners.NatoSocketListener
import org.json.JSONObject
import javax.inject.Inject

class NatoScenarioSocketManager @Inject constructor(
    private val socket: Socket
) {
    private lateinit var natoSocketListener: NatoSocketListener

    companion object {
        const val TAG = "LOG"
        const val LIVE_KIT_TOKEN = "livekit_token"
        const val HANDLE = "game_handle"
        const val USERS_DATA = "users_data"
        const val GAME_EVENT = "game_event"
        const val GAME_ACTION = "game_action"
        const val IN_GAME_TURN_SPEECH = "in_game_turn_speech"
        const val START_SPEECH = "start_speech"
        const val SPEECH_TIME_UP = "speech_time_up"
        const val VOTE = "vote"
        const val REPORT = "report"
        const val MAFIA_VISITATION = "mafia_visitation"
        const val ACCEPT_CHALLENGE = "accept_challenge"
        const val USE_ABILITY = "use_ability"
        const val MAFIA_SHOT = "mafia_shot"
        const val CURRENT_SPEECH = "current_speech"
        const val CURRENT_SPEECH_END = "current_speech_end"
        const val MAFIA_DECISION = "mafia_decision"
        const val DETECTIVE_INQUIRY = "detective_inquiry"
        const val USERS_CHALLENGE_STATUS = "users_challenge_status"
        const val USING_SPEECH_OPTIONS = "using_speech_options"
        const val USER_REQUEST_SPEECH_OPTIONS = "user_request_speech_options"
        const val SPEECH_OPTION_MSG = "speech_option_msg"
        const val REQUEST_SPEECH_OPTIONS = "request_speech_options"
        const val REPORT_GUN = "report_gun"
        const val GUN_STATUS = "gun_status"
        const val DAY_USING_GUN = "day_using_gun"
        const val USED_GUN = "used_gun"
        const val DAY_INQUIRY = "day_inquiry"
        const val DAY_INQUIRY_RESULT = "day_inquiry_result"
        const val CHAOS_NOTIF_VOTE_TIME = "chaos_notif_vote_time"
        const val CHAOS_VOTE_RESULT = "chaos_vote_result"
        const val CHAOS_VOTE = "chaos_vote"
        const val CHAOS_USER_SPEECH = "chaos_user_speech"
        const val CHAOS_ALL_SPEECH = "chaos_all_speech"
        const val CHAOS_ALL_SPEECH_END = "chaos_all_speech_end"
        const val TURN_TO_SHAKE = "turn_to_shake"
        const val MOD_CHARACTERS = "mod_characters"
        const val MOD_DATA = "mod_data"
        const val MOD_STATUS = "mod_status"
        const val MOD_PANEL_EVENTS = "mod_panel_events"
        const val END_GAME_RESULT = "end_game_result"
        const val LAST_DECISION = "last_decision"
        const val MAFIA_SPEECH = "mafia_speech"
        const val MAFIA_SPEECH_END = "mafia_speech_end"
        const val ABANDON = "abandon"
        const val PLAYER_SHOW_CHARACTER = "player_show_character"
        const val ACTION_END = "action_end"
        const val PLAY_VOICE = "play_voice"
    }

    fun init(natoSocketListener: NatoSocketListener) {
        this.natoSocketListener = natoSocketListener

        // room id
        socket.on(LIVE_KIT_TOKEN) {
            Log.i(TAG, "live kit token: ${it[0]}")
            natoSocketListener.onRoomId(it[0].toString())
        }

        // users data
        socket.on(USERS_DATA) {
            Log.i("LOG", "users data : ${it[0]}")
            natoSocketListener.onUsersData(it[0].toString())
        }

        // game event
        socket.on(GAME_EVENT) {
            Log.i("LOG", "game event: ${it[0]} ")
            natoSocketListener.onGameEvent(it[0].toString())
        }

        // game action
        socket.on(GAME_ACTION) {
            Log.i(TAG, "user actions : ${it[0]}")
            natoSocketListener.onUserAction(it[0].toString())
        }

        // in game turn speech
        socket.on(IN_GAME_TURN_SPEECH) {
            natoSocketListener.onSpeechTurn(it[0].toString())
        }

        socket.on(MAFIA_SPEECH) {
            Log.i(TAG, "mafia speech: ${it[0]}")
            natoSocketListener.onMafiaSpeech(it[0].toString())
        }

        socket.on(MAFIA_SPEECH_END) {
            Log.i(TAG, "mafia speech end: ")
            natoSocketListener.onMafiaSpeechEnd()
        }

        // start speech
        socket.on(START_SPEECH) {
            natoSocketListener.onStartSpeech()
        }

        // speech time up
        socket.on(SPEECH_TIME_UP) {
            Log.i(TAG, "speech time up: ${it[0]}")
            natoSocketListener.onSpeechTimeUp(it[0].toString())
        }

        // vote
        socket.on(VOTE) {
            natoSocketListener.onVoteToPlayer(it[0].toString())
        }

        // report
        socket.on(REPORT) {
            Log.i(TAG, "report: ${it[0]}")
            natoSocketListener.onReport(it[0].toString())
        }

        // mafia visitation
        socket.on(MAFIA_VISITATION) {
            natoSocketListener.onMafiaVisitation(it[0].toString())
        }

        // accept challenge
        socket.on(ACCEPT_CHALLENGE) {
            natoSocketListener.onAcceptChallenge()
        }
        // night use ability
        socket.on(USE_ABILITY) {
            natoSocketListener.onUseAbility(it[0].toString())
        }
        // mafia shot
        socket.on(MAFIA_SHOT) {
            natoSocketListener.onMafiaShot(it[0].toString())
        }
        // current player speech
        socket.on(CURRENT_SPEECH) {
            natoSocketListener.onCurrentSpeech(it[0].toString())
        }
        // current player speech end
        socket.on(CURRENT_SPEECH_END) {
            Log.i(TAG, "current speech end: ${it[0]}")
            natoSocketListener.onCurrentSpeechEnd(it[0].toString())
        }
        // shot or nato ? decision
        socket.on(MAFIA_DECISION) {
            natoSocketListener.onMafiaDecision(it[0].toString())
        }
        // detective inquiry at night
        socket.on(DETECTIVE_INQUIRY) {
            Log.i(TAG, "detective inquiry: ${it[0]}")
            natoSocketListener.onDetectiveInquiry(it[0].toString())
        }
        // users challenge status
        socket.on(USERS_CHALLENGE_STATUS) {
            Log.i(TAG, "challenge: ${it[0]}")
            natoSocketListener.onUsersChallengeStatus(it[0].toString())
        }
        // target cover about wanna use them ?
        socket.on(USING_SPEECH_OPTIONS) {
            natoSocketListener.onUsingSpeechOptions(it[0].toString())
        }
        // broadcast to all the current player standing on defence decide to target cover
        socket.on(USER_REQUEST_SPEECH_OPTIONS) {
            natoSocketListener.onWhichUserRequestSpeechOptions(it[0].toString())
        }
        // tell to current user on defence which going to select target or cover or about
        socket.on(SPEECH_OPTION_MSG) {
            natoSocketListener.onSpeechOptionMsg(it[0].toString())
        }
        // broadcast to all that current player standing on defence now want to target or cover or about
        socket.on(REQUEST_SPEECH_OPTIONS) {
            natoSocketListener.onRequestSpeechOptions(it[0].toString())
        }
        // report gun in night
        socket.on(REPORT_GUN) {
            natoSocketListener.onReportGun()
        }
        // report gun status
        socket.on(GUN_STATUS) {
            natoSocketListener.onGunStatus(it[0].toString())
        }
        // day player decide to using his gun
        socket.on(DAY_USING_GUN) {
            natoSocketListener.onDayUsingGun(it[0].toString())
        }
        // player shot to user on day broadcast to others
        socket.on(USED_GUN) {
            natoSocketListener.onDayUsedGun(it[0].toString())
        }
        // are users want to day inquiry ?
        socket.on(DAY_INQUIRY) {
            natoSocketListener.onDayInquiry(it[0].toString())
        }
        // day inquiry result
        socket.on(DAY_INQUIRY_RESULT) {
            natoSocketListener.onDayInquiryResult(it[0].toString())
        }
        socket.on(PLAYER_SHOW_CHARACTER) {
            natoSocketListener.onPlayerShowCharacter()
        }
        // notification chaos vote time
        socket.on(CHAOS_NOTIF_VOTE_TIME) {
            natoSocketListener.onChaosVoteTimeNotification()
        }
        // chaos vote result per user
        socket.on(CHAOS_VOTE) {
            natoSocketListener.onChaosVote(it[0].toString())
        }
        socket.on(CHAOS_VOTE_RESULT) {
            natoSocketListener.onChaosVoteResult(it[0].toString())
        }
        // chaos turn to shake
        socket.on(TURN_TO_SHAKE){
            natoSocketListener.onChaosTurnToShake(it[0].toString())
        }
        // which user speech in chaos all speech
        socket.on(CHAOS_USER_SPEECH) {
            natoSocketListener.onChaosUserSpeech(it[0].toString())
        }

        socket.on(CHAOS_ALL_SPEECH) {
            natoSocketListener.onChaosAllSpeech()
        }

        socket.on(CHAOS_ALL_SPEECH_END) {
            natoSocketListener.onChaosAllSpeechEnd()
        }

        // get character of users for moderator
        socket.on(MOD_CHARACTERS) {
//            Log.i(TAG, "moderator characters: ${it[0]}")
            natoSocketListener.onUsersCharacters(it[0].toString())
        }

        // get moderator data
        socket.on(MOD_DATA) {
//            Log.i(TAG, "mod data : ${it[0]}")
            natoSocketListener.onModeratorData(it[0].toString())
        }

        socket.on(MOD_STATUS) {
//            Log.i(TAG, "moderator data: ${it[0]}")
            natoSocketListener.onModeratorStatus(it[0].toString())
        }

        // moderator log panel
        socket.on(MOD_PANEL_EVENTS) {
            natoSocketListener.onModeratorPanelEvent(it[0].toString())
        }

        socket.on(END_GAME_RESULT) {
            natoSocketListener.onEndGameResult(it[0].toString())
        }

        socket.on(LAST_DECISION) {
            Log.i(TAG, "last_decision: ${it[0]}")
            natoSocketListener.onLastDecision(it[0].toString())
        }

        socket.on(ABANDON){
            natoSocketListener.onAbandon()
        }

        socket.on(ACTION_END){
            natoSocketListener.onActionEnd()
        }

        socket.on(PLAY_VOICE){
            Log.i("LOG", "play voice: ${it[0]}")
            natoSocketListener.onPlayVoice(it[0].toString())
        }

        addSocketListenersToArray()
    }

    private fun addSocketListenersToArray() {
        SocketManager.clearNatoSocketGameArray()
        SocketManager.addNatoGameSocket(LIVE_KIT_TOKEN)
        SocketManager.addNatoGameSocket(USERS_DATA)
        SocketManager.addNatoGameSocket(GAME_EVENT)
        SocketManager.addNatoGameSocket(GAME_ACTION)
        SocketManager.addNatoGameSocket(IN_GAME_TURN_SPEECH)
        SocketManager.addNatoGameSocket(START_SPEECH)
        SocketManager.addNatoGameSocket(SPEECH_TIME_UP)
        SocketManager.addNatoGameSocket(VOTE)
        SocketManager.addNatoGameSocket(REPORT)
        SocketManager.addNatoGameSocket(MAFIA_VISITATION)
        SocketManager.addNatoGameSocket(ACCEPT_CHALLENGE)
        SocketManager.addNatoGameSocket(USE_ABILITY)
        SocketManager.addNatoGameSocket(MAFIA_SHOT)
        SocketManager.addNatoGameSocket(CURRENT_SPEECH_END)
        SocketManager.addNatoGameSocket(CURRENT_SPEECH)
        SocketManager.addNatoGameSocket(MAFIA_DECISION)
        SocketManager.addNatoGameSocket(DETECTIVE_INQUIRY)
        SocketManager.addNatoGameSocket(USERS_CHALLENGE_STATUS)
        SocketManager.addNatoGameSocket(USING_SPEECH_OPTIONS)
        SocketManager.addNatoGameSocket(USER_REQUEST_SPEECH_OPTIONS)
        SocketManager.addNatoGameSocket(SPEECH_OPTION_MSG)
        SocketManager.addNatoGameSocket(REQUEST_SPEECH_OPTIONS)
        SocketManager.addNatoGameSocket(REPORT_GUN)
        SocketManager.addNatoGameSocket(GUN_STATUS)
        SocketManager.addNatoGameSocket(DAY_USING_GUN)
        SocketManager.addNatoGameSocket(USED_GUN)
        SocketManager.addNatoGameSocket(DAY_INQUIRY)
        SocketManager.addNatoGameSocket(DAY_INQUIRY_RESULT)
        SocketManager.addNatoGameSocket(CHAOS_NOTIF_VOTE_TIME)
        SocketManager.addNatoGameSocket(CHAOS_VOTE)
        SocketManager.addNatoGameSocket(CHAOS_ALL_SPEECH)
        SocketManager.addNatoGameSocket(CHAOS_ALL_SPEECH_END)
        SocketManager.addNatoGameSocket(MOD_CHARACTERS)
        SocketManager.addNatoGameSocket(MOD_DATA)
        SocketManager.addNatoGameSocket(MOD_STATUS)
        SocketManager.addNatoGameSocket(MOD_PANEL_EVENTS)
        SocketManager.addNatoGameSocket(END_GAME_RESULT)
        SocketManager.addNatoGameSocket(LAST_DECISION)
        SocketManager.addNatoGameSocket(MAFIA_SPEECH)
        SocketManager.addNatoGameSocket(MAFIA_SPEECH_END)
        SocketManager.addNatoGameSocket(ABANDON)
        SocketManager.addNatoGameSocket(ACTION_END)
        SocketManager.addNatoGameSocket(TURN_TO_SHAKE)
        SocketManager.addNatoGameSocket(PLAY_VOICE)
    }

    fun sendData(obj: JSONObject) {
        socket.emit(HANDLE, obj)
    }


    fun userEndGame() {
        socket.emit("user_end_game", null)
    }

    fun turnOffGameSocket() {
        SocketManager.clearNatoGameSocket()
    }
}