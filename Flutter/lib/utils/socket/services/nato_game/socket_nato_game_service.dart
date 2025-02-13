import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/nato_game/socket_nato_game_listeners.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketNatoGameService extends GetxService {
  final Socket _socket = Get.find();
  Future<SocketNatoGameService> init() async {
    return this;
  }

  void emit(String key, Map? json) {
    _socket.emit(key, json);
  }

  static const List<String> _events = [
    'livekit_token',
    'users_data',
    'game_event',
    'game_action',
    'start_speech',
    'speech_time_up',
    'mafia_visitation',
    'mafia_speech',
    'mafia_speech_end',
    'vote',
    'report',
    'low_level_report',
    'accept_challenge',
    'use_ability',
    'mafia_shot',
    'current_speech',
    'current_speech_end',
    'mafia_decision',
    'detective_inquiry',
    'users_challenge_status',
    'using_speech_options',
    'report_gun',
    'gun_status',
    'player_show_character',
    'chaos_vote',
    'chaos_vote_result',
    'turn_to_shake',
    'chaos_user_speech',
    'chaos_all_speech',
    'chaos_all_speech_end',
    'last_decision',
    'end_game_result',
    'action_end',
    'play_voice',
    'abandon',
    'grant_permission',
    'become_volunteer',
    'clear_chaos_record'
  ];
  void addListener(SocketNatoGameListener listener) async {
    await clearSockets();
    _socket.on('livekit_token', (data) => listener.onLiveKitToken(data));
    _socket.on('users_data', (data) => listener.onUsersData(data));
    _socket.on('game_event', (data) => listener.onGameEvent(data));
    _socket.on('game_action', (data) => listener.onGameAction(data));
    _socket.on('mafia_speech', (data) => listener.onMafiaSpeech(data));
    _socket.on('mafia_speech_end', (data) => listener.onMafiaSpeechEnd());
    _socket.on('start_speech', (data) => listener.onStartSpeech());
    _socket.on('speech_time_up', (data) => listener.onSpeechTimeUp(data));
    _socket.on('vote', (data) => listener.onVote(data));
    _socket.on('report', (data) => listener.onReport(data));
    _socket.on('low_level_report', (data) => listener.onLowLevelReport(data));
    _socket.on('mafia_visitation', (data) => listener.onMafiaVisitation(data));
    _socket.on('accept_challenge', (data) => listener.onAcceptChallenge());
    _socket.on('use_ability', (data) => listener.onUseNightAbility(data));
    _socket.on('mafia_shot', (data) => listener.onMafiaShot(data));
    _socket.on(
        'current_speech', (data) => listener.onCurrentPlayerTalking(data));
    _socket.on('current_speech_end',
        (data) => listener.onCurrentPlayerTalkingEnd(data));
    _socket.on('mafia_decision', (data) => listener.onMafiaDecision(data));
    _socket.on('detective_inquiry',
        (data) => listener.onDetectiveInquiryResponse(data));
    _socket.on('users_challenge_status',
        (data) => listener.onUsersChallengeStatus(data));
    _socket.on(
        'using_speech_options', (data) => listener.onUsingSpeechOptions(data));
    _socket.on('report_gun', (data) => listener.onReportGun());
    _socket.on('gun_status', (data) => listener.onDayGunStatus(data));
    _socket.on('used_gun', (data) => listener.onDayUsedGun(data));
    _socket.on(
        'player_show_character', (data) => listener.onPlayerShowCharacter());
    _socket.on('chaos_vote', (data) => listener.onChaosVote(data));
    _socket.on('chaos_vote_result', (data) => listener.onChaosVoteResult(data));
    _socket.on(
        'turn_to_shake', (data) => listener.onChaosTurnToHandShake(data));
    _socket.on('chaos_user_speech', (data) => listener.onChaosSpeechUser(data));
    _socket.on('chaos_all_speech', (data) => listener.onChaosAllSpeech(data));
    _socket.on(
        'chaos_all_speech_end', (data) => listener.onChaosAllSpeechEnd());
    _socket.on('end_game_result', (data) => listener.onEndGameResult(data));
    _socket.on('last_decision', (data) => listener.onChaosLastDecision(data));
    _socket.on('abandon', (data) => listener.onAbandon());
    _socket.on('play_voice', (data) => listener.onPlayVoice(data));
    _socket.on('action_end', (data) => listener.onActionEnd());
    _socket.on('grant_permission', (data) => listener.onGrantPermission(data));
    _socket.on('become_volunteer', (data) => listener.onBecomeVolunteer(data));
    _socket.on('clear_chaos_record', (data) => listener.onClearChaosRecord());
  }

  Future<void> clearSockets() async {
    return await Future.forEach(_events, (element) => _socket.off(element));
  }
}
