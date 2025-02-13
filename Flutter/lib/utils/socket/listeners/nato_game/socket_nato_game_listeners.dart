abstract class SocketNatoGameListener {
  /// livekit token
  void onLiveKitToken(dynamic data);

  /// user basics data
  void onUsersData(dynamic data);

  /// game event
  void onGameEvent(dynamic data);

  /// game actions
  void onGameAction(dynamic data);

  /// mafia speech
  void onMafiaSpeech(dynamic data);

  /// mafia speech end
  void onMafiaSpeechEnd();

  /// self player speech time
  void onStartSpeech();

  /// self player speech times up
  void onSpeechTimeUp(dynamic data);

  /// vote
  void onVote(dynamic data);

  /// general reports coming from server
  void onReport(dynamic data);

  /// low level report to prevent overlay
  void onLowLevelReport(dynamic data);

  /// mafia visitation
  void onMafiaVisitation(dynamic data);

  /// self challenge accepted
  void onAcceptChallenge();

  /// night use ability
  void onUseNightAbility(dynamic data);

  /// mafia shot
  void onMafiaShot(dynamic data);

  /// current playing talking
  void onCurrentPlayerTalking(dynamic data);

  /// current player talking end
  void onCurrentPlayerTalkingEnd(dynamic data);

  /// make a decision ! shot or nato
  void onMafiaDecision(dynamic data);

  /// detective inquiry at night
  void onDetectiveInquiryResponse(dynamic data);

  /// users challenge status
  void onUsersChallengeStatus(dynamic data);

  /// user decide to use speech options like target , cover
  void onUsingSpeechOptions(dynamic data);

  /// report gun in night
  void onReportGun();

  /// day gun status
  void onDayGunStatus(dynamic data);

  /// day gun used
  void onDayUsedGun(dynamic data);

  /// show character
  void onPlayerShowCharacter();

  /// chaos vote time per user
  void onChaosVote(dynamic data);

  /// chaos vote result per user
  void onChaosVoteResult(dynamic data);

  /// chaos turn to hand shake
  void onChaosTurnToHandShake(dynamic data);

  /// current user speech on chaos
  void onChaosSpeechUser(dynamic data);

  /// chaos all speech
  void onChaosAllSpeech(dynamic data);

  /// chaos all speech end
  void onChaosAllSpeechEnd();

  /// chaos last decision
  void onChaosLastDecision(dynamic data);

  /// end game result
  void onEndGameResult(dynamic data);

  /// action end or disable all user actions
  void onActionEnd();

  /// abandon
  void onAbandon();

  /// voice commands
  void onPlayVoice(dynamic data);

  /// defender player on vote , has permit or not to select some volunteer
  void onGrantPermission(dynamic data);

  /// which user wont to become volunteer to target , cover or about
  void onBecomeVolunteer(dynamic data);

  /// clear chaos hand shake records
  void onClearChaosRecord();
}
