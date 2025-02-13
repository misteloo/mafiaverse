package ir.greendex.mafia.game.nato.listeners

import ir.greendex.mafia.entity.game.general.ChaosTurnToShakeEntity
import ir.greendex.mafia.entity.game.general.ChaosUserSpeechEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteEntity
import ir.greendex.mafia.entity.game.general.ChaosVoteResultEntity
import ir.greendex.mafia.entity.game.general.CurrentSpeechEntity
import ir.greendex.mafia.entity.game.general.DayInquiryEntity
import ir.greendex.mafia.entity.game.general.DetectiveInquiryEntity
import ir.greendex.mafia.entity.game.general.EndGameResultEntity
import ir.greendex.mafia.entity.game.general.GameActionEntity
import ir.greendex.mafia.entity.game.general.GodFatherShotEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorEntity
import ir.greendex.mafia.entity.game.general.InGameModeratorStatus
import ir.greendex.mafia.entity.game.general.InGameTurnSpeechEntity
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.general.ModeratorLogEntity
import ir.greendex.mafia.entity.game.general.ReportEntity
import ir.greendex.mafia.entity.game.nato.NatoMafiaVisitationEntity
import ir.greendex.mafia.entity.game.general.RequestSpeechOptionsEntity
import ir.greendex.mafia.entity.game.general.SpeechEndEntity
import ir.greendex.mafia.entity.game.general.SpeechOptionMsgEntity
import ir.greendex.mafia.entity.game.general.UsersChallengeStatusEntity
import ir.greendex.mafia.entity.game.general.UsersCharacterEntity
import ir.greendex.mafia.entity.game.general.UsingSpeechOptionsEntity
import ir.greendex.mafia.entity.game.general.VoteEntity
import ir.greendex.mafia.entity.game.general.WhichUserRequestSpeechOptionEntity
import ir.greendex.mafia.entity.game.general.enum_cls.NatoGameEventEnum
import ir.greendex.mafia.entity.game.nato.DayUsedGunEntity
import ir.greendex.mafia.entity.game.nato.GunStatusEntity
import ir.greendex.mafia.entity.game.nato.MafiaDecisionEntity
import ir.greendex.mafia.entity.game.nato.MafiaSpeechEntity
import ir.greendex.mafia.entity.game.nato.NatoUseAbilityEntity

interface NatoViewModelListener {
    fun onRoomId(token:String)
    fun onMafiaSpeech(data: MafiaSpeechEntity)
    fun onMafiaSpeechEnd()
    fun onUsersData(users:List<InGameUsersDataEntity.InGameUserData>)
    fun onGameEvent(event:NatoGameEventEnum)
    fun onGameAction(data:List<GameActionEntity.GameActionData>)
    fun onStartSpeech()
    /**
     * This only calls to speaking user
     * */
    fun onSpeechTimeUp(data:SpeechEndEntity.SpeechEndData)
    fun onVoteToPlayer(data: VoteEntity.VoteData)
    fun onChallengeAccepted()
    fun onInGameTurnSpeechQueue(it:InGameTurnSpeechEntity)
    fun onUseAbility(it:NatoUseAbilityEntity.NatoUseAbilityData)
    fun onCurrentSpeech(it:CurrentSpeechEntity)
    /**
    * This only calls to other users who listening to him .
    * */
    fun onCurrentSpeechEnd(data:SpeechEndEntity.SpeechEndData)
    fun onMafiaDecision(data:MafiaDecisionEntity)
    fun onMafiaShot(it:GodFatherShotEntity)
    fun onDetectiveInquiry(it:DetectiveInquiryEntity)
    fun onUsersChallengeStatus(userChallengeStatus:List<UsersChallengeStatusEntity.UserChallengeStatusData>)
    fun onMafiaVisitation(mafiaList:List<NatoMafiaVisitationEntity>)
    fun onSpeechOptions(speechOption:UsingSpeechOptionsEntity.UsingSpeechOptionsData)
    fun onWhichUserRequestSpeechOption(which:WhichUserRequestSpeechOptionEntity.WhichUserRequestSpeechOptionData)
    fun onSpeechOptionMsg(msg:SpeechOptionMsgEntity.SpeechOptionMsgData)
    fun onRequestSpeechOption(data:RequestSpeechOptionsEntity.RequestSpeechOptionsData)
    fun onReportGun()
    fun onGunStatus(data:GunStatusEntity.GunStatusData)
    fun onDayUsingGun(userId:String)
    fun onDayUsedGun(data:DayUsedGunEntity.DayUsedGunData)
    fun onReport(data:ReportEntity.ReportData)
    fun onDayInquiry(data:DayInquiryEntity.DayInquiryData)
    fun onDayInquiryResult(data:DayInquiryEntity.DayInquiryData)
    fun onChaosVoteTimeNotification()
    fun onChaosVoteResult(data:ChaosVoteResultEntity)
    fun onChaosVote(data:ChaosVoteEntity.ChaosVoteData)
    fun onChaosUserSpeech(data:List<ChaosUserSpeechEntity.ChaosUserSpeechData>)
    fun onChaosAllSpeech()
    fun onChaosAllSpeechEnd()
    fun onChaosTurnToShake(data:ChaosTurnToShakeEntity.ChaosTurnToShakeData)
    fun onUsersCharacters(data:List<UsersCharacterEntity.UsersCharacterData>)
    fun onModeratorData(data:InGameModeratorEntity?)
    fun onModeratorStatus(data:InGameModeratorStatus.InGameModeratorStatusData)
    fun onModeratorPanelEvent(data:ModeratorLogEntity.ModeratorLogData)
    fun onEndGameResult(data:EndGameResultEntity.EndGameResultData)
    fun onLastDecision(data:ChaosVoteEntity)
    fun onPlayerShowCharacter()
    fun onAbandon()
    fun onActionEnd()
}