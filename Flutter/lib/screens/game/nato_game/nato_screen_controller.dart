import 'dart:async';
import 'dart:convert';

import 'package:audioplayers/audioplayers.dart';
import 'package:bot_toast/bot_toast.dart';
import 'package:circular_countdown_timer/circular_countdown_timer.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:livekit_client/livekit_client.dart';
import 'package:mafia/models/game/end_game_result_model.dart';
import 'package:mafia/models/game/in_game_user_identity_model.dart';
import 'package:mafia/models/game/in_game_user_model.dart';
import 'package:mafia/models/game/mafia_visitation_model.dart';
import 'package:mafia/models/game/nato/nato_night_act_model.dart';
import 'package:mafia/screens/game/nato_game/nato_screen.dart';
import 'package:mafia/utils/constants/encrypt.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/constants/values.dart';
import 'package:mafia/utils/socket/listeners/nato_game/socket_nato_game_listeners.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:mafia/utils/voice/live_kit_manager.dart';
import 'package:mafia/utils/widget/custom_snack.dart';
import 'package:vibration/vibration.dart';

class NatoScreenController extends GetxController
    implements SocketNatoGameListener {
  void _getArgs() {
    // binding room
    _bindingRoom();

    characterImage = Get.arguments['characterImage'];
    characterName = Get.arguments['characterName'];
    gameId = Get.arguments['gameId'];
    myUserId = Get.arguments['userId'];
    serverCharacter = Get.arguments['serverCharacter'];

    update(['userCharacter']);

    if (Get.arguments['fromReconnect']) {
      List<MafiaVisitationModel> argMafiaList = Get.arguments['mafiaList'];
      bool argDayGun = Get.arguments['gunStatus'];

      // users
      List<InGameUserModel> argUsers = Get.arguments['users'];
      //users
      users.addAll(argUsers);
      // mafia list
      if (argMafiaList.isNotEmpty) {
        mafiaList.addAll(argMafiaList);

        for (var mafia in mafiaList) {
          users
              .singleWhere(
                  (element) => element.userIdentity.user_id == mafia.userId)
              .mafiaCharacterImg = mafia.characterImage;
        }

        update(['mafiaList']);
      }
      // day gun
      if (argDayGun) {
        dayGunAvailable = true;
        update(['dayGunStatus']);
      }
      // room id
      _liveKit.connectToRoom(_gameRoom, Get.arguments['roomId']);

      gameStarted = true;
      // bind to views
      update(['users', 'gameStarted']);
      // game event
      _modifyGameEvent(Get.arguments['gameEvent'] as String);
    }
    // ready
    _readyToGame();
  }

  // injections
  final LiveKitManager _liveKit = Get.find();
  final SocketManager _socketManager = Get.find();
  final HashRouting _router = Get.find();
  final AudioPlayer _audioPlayer = Get.find();

  // instances

  /// server character means the real name of character which working client and server
  late String serverCharacter;

  /// waiting to other players join to game
  bool gameStarted = false;

  ///live kit room
  late Room _gameRoom;

  /// users data
  final List<InGameUserModel> users =
      List<InGameUserModel>.empty(growable: true);

  /// game event which would be `day`,`night`,`action`,`speech`,`vote`,`chaos`,`end`, or `none`
  String gameEvent = 'none';

  /// main game event which could be `day` , `vote` , `night` , `chaos` and  `end`
  String mainGameEvent = 'none';

  /// character name
  late String? characterName;

  /// character image
  late String? characterImage;

  /// game id
  late String? gameId;

  /// user id
  late String? myUserId;

  /// Rx user like or dislike
  var userLikeDislikeActive = false.obs;

  /// Rx mic toggle
  var micToggle = false.obs;

  /// count down timer controller
  final CountDownController gameTimerController = CountDownController();

  /// day gun status
  var dayGunAvailable = false;

  /// dat gun is activated
  var dayGunActive = false;

  /// Rx day gun using time left
  var dayGunUsingTimeLeft = 15.obs;

  /// each mafia player can toggle to see teammates
  var mafiaListVisibilityToggling = false;

  /// Rx mafia list
  List<MafiaVisitationModel> mafiaList =
      List<MafiaVisitationModel>.empty(growable: true);

  /// Rx shift to next player
  bool nextPlayerAvailable = true;

  /// Rx challenge request count down timer status
  var activeChallengeRequestTimer = false.obs;

  /// challenge request count down timer controller
  final CountDownController challengeRequestTimerController =
      CountDownController();

  /// defender play has permission to select any one or not
  bool targetCoverPermission = false;

  /// chaos act count down timer
  Timer? _chaosTimer;

  // functions

  void returnToHomeScreen() {
    _exitFromGame(customExit: true);
  }

  /// my user id
  String? getMyUserId() => myUserId;

  void _bindingRoom() {
    _gameRoom = _liveKit.createRoom;
  }

  void _initGameListeners() {
    _socketManager.getNatoGameSocket.addListener(this);
  }

  bool checkSelfAlive() {
    var self = users
        .singleWhere((element) => element.userIdentity.user_id == myUserId);
    return self.alive;
  }

  /// ready to game
  void _readyToGame() {
    Map<String, String> data = {'op': 'ready_to_game'};
    _socketManager.getNatoGameSocket.emit('game_handle', data);
  }

  /// send current user action to server
  void selfUserActions(
      {bool challengeRequest = false,
      bool like = false,
      bool dislike = false}) {
    late Map<String, String> data;
    if (like) {
      data = {'action': 'like'};
      userLikeDislikeActive(true);
    } else if (dislike) {
      data = {'action': 'dislike'};
      userLikeDislikeActive(true);
    } else if (challengeRequest) {
      data = {'action': 'challenge_request'};
    }

    Map<String, dynamic> op = {'op': 'user_action', 'data': data};

    _socketManager.getNatoGameSocket.emit('game_handle', op);

    // timer
    if (challengeRequest) {
      _challengeRequestCountDown();
    }
  }

  // challenge Request Timer
  void _challengeRequestCountDown() {
    activeChallengeRequestTimer.value = true;
  }

  /// toggle local mic to open and close
  void livekitMicToggle() {
    micToggle.toggle();
    _liveKit.participantSpeaking(_gameRoom, micToggle.value);
  }

  /// defender player decide to use speech option or not
  void _usingSpeechOptions(bool use) {
    Map<String, dynamic> body = {'using_option': use};

    Map<String, dynamic> op = {'op': 'using_speech_options', 'data': body};

    // emit
    _socketManager.getNatoGameSocket.emit('game_handle', op);
  }

  /// jump to next player
  void nextSpeech() {
    nextPlayerAvailable = false;
    _stopGameTimer();
    gameEvent = 'none';
    micToggle.value = false;
    _liveKit.participantSpeaking(_gameRoom, false);
    update(['actionBoard']);
    Map<String, String> data = {'op': 'next_speech'};
    _socketManager.getNatoGameSocket.emit('game_handle', data);
  }

  /// activating day gun
  void activeDayGun() {
    Map<String, dynamic> body = {'user_id': myUserId};

    Map<String, dynamic> op = {'op': 'day_using_gun', 'data': body};

    _socketManager.getNatoGameSocket.emit('game_handle', op);
    // start timer
    _runDayGunTimer();
  }

  // day gun timer
  void _runDayGunTimer() {
    dayGunUsingTimeLeft.value = 15;
    dayGunActive = true;
    Timer.periodic(const Duration(seconds: 1), (timer) {
      if (dayGunUsingTimeLeft.value == 0) {
        timer.cancel();
        dayGunActive = false;
        dayGunAvailable = false;
        update(['dayGunStatus']);
      } else {
        dayGunUsingTimeLeft.value--;
      }
    });
  }

  /// shot day gun
  void shotWithDayGun(String userId) {
    Map<String, dynamic> body = {'user_id': userId};

    Map<String, dynamic> op = {'op': 'rifle_gun_shot', 'data': body};
    _socketManager.getNatoGameSocket.emit('game_handle', op);

    // play shot sound
    _audioPlayer.play(AssetSource('pistol_sound.mp3'));

    // disable gun status
    dayGunActive = false;
    dayGunAvailable = false;
    update(['dayGunStatus']);
  }

  /// exit from game , custom exit or server command to abandon
  void _exitFromGame({required bool customExit}) async {
    if (customExit) {
      // custom abandon from game
      _socketManager.getNatoGameSocket.emit('abandon', null);
    }
    // disable mic
    _gameRoom.localParticipant?.setMicrophoneEnabled(false);
    // clear sockets
    await _socketManager.getNatoGameSocket.clearSockets();
    // dc room
    await _liveKit.disConnectRoom(_gameRoom);
    await _liveKit.dispose(_gameRoom);
    // close any dialogs opening
    if (Get.isDialogOpen ?? false) {
      Get.back();
    }

    if (Get.isBottomSheetOpen ?? false) {
      Get.back();
    }

    // return home
    Get.until(
        (route) => Get.currentRoute == _router.getRoute('main_btm_nav_holder'));
  }

  /// main game timer
  void _startGameTimer(int time) {
    gameTimerController.restart(duration: time);
    update(['timer']);
  }

  /// stop game timer
  void _stopGameTimer() {
    gameTimerController.reset();
    update(['timer']);
  }

  /// vote to player
  void voteToPlayer() {
    gameEvent = 'none';
    update(['actionBoard']);
    Map<String, String> json = {'op': 'vote'};
    _socketManager.getNatoGameSocket.emit('game_handle', json);
  }

  /// accept challenge request
  void acceptChallenge({required String userId}) {
    var me = users.firstWhereOrNull(
        (element) => element.userIdentity.user_id == myUserId);

    if (me != null && me.speech) {
      var data = {
        'user_id': userId,
      };
      var json = {'op': 'accept_challenge', 'data': data};
      _socketManager.getNatoGameSocket.emit('game_handle', json);
    }
  }

  /// get player detail
  InGameUserModel get getMyDetails =>
      users.singleWhere((element) => myUserId == element.userIdentity.user_id);

  /// mafia toggling view instead user profile
  void mafiaToggleView() {
    mafiaListVisibilityToggling = !mafiaListVisibilityToggling;
    update(['mafiaToggle', 'showMafiaList']);
  }

  /// mafiaDecision to night act
  void mafiaDecision(String decision) {
    Map<String, bool> body = {
      'shot': decision == 'shot',
      'nato': decision == 'nato',
      'role': Get.arguments['serverCharacter']
    };

    Map<String, dynamic> json = {'op': 'mafia_decision', 'data': body};
    _socketManager.getNatoGameSocket.emit('game_handle', json);
  }

  /// dismiss night idle bs sheet
  void _hideNightIdle() {
    if (Get.rawRoute?.settings.name == 'nightIdle') {
      Get.back();
    }
  }

  /// send night act to server
  void _sendNightAct(
    List<NatoNightActModel> selectedUsers,
    bool isMafiaShot,
  ) {
    List<Map> jsonArray = List<Map>.empty(growable: true);
    for (var element in selectedUsers) {
      Map<String, dynamic> userIds = {
        'user_id': element.userId,
        'act': element.natoAct ? element.guessCharacter : element.gunKind
      };
      jsonArray.add(userIds);
    }
    Map<String, dynamic> data = {'users': jsonArray, 'role': serverCharacter};

    Map<String, dynamic> op = {
      'op': isMafiaShot ? 'mafia_shot' : 'night_act',
      'data': data
    };

    _socketManager.getNatoGameSocket.emit('game_handle', op);
  }

  /// vibrate
  void _startVibrate() async {
    if (await Vibration.hasVibrator() == true) {
      Vibration.vibrate();
    }
  }

  void selectTargetCoverVolunteer(String userId) {
    if (targetCoverPermission) {
      Map<String, dynamic> body = {'user_id': userId};
      Map<String, dynamic> op = {'op': 'select_volunteer', 'data': body};

      _socketManager.getNatoGameSocket.emit('game_handle', op);

      //toggle
      targetCoverPermission = false;
    }
  }

  @override
  void onLiveKitToken(data) {
    var token = data['token'];
    // connect to live kit
    _liveKit.connectToRoom(_gameRoom, token);
  }

  @override
  void onPlayVoice(data) {
    var parse = data['data'];
    _audioPlayer.play(AssetSource('${parse['voice_id'] as String}.mp3'));
  }

  @override
  void onPlayerShowCharacter() {
    characterImage = 'images/citizen.png';
    characterName = 'شهروند ساده';
    serverCharacter = 'citizen';
    update(['userCharacter']);
  }

  @override
  void onReport(data) async {
    dynamic parse = data['data'];
    String msg = parse['msg'];
    int timer = parse['timer'];
    String? userId = parse['user_id'];
    InGameUserModel? user = users
        .firstWhereOrNull((element) => element.userIdentity.user_id == userId);

    // show dialog msg
    BotToast.showCustomNotification(
      toastBuilder: (cancelFunc) {
        return InGameMsgDialog(
          msg: msg,
          user: user,
        );
      },
      duration: Duration(seconds: timer),
      align: Alignment.center,
    );
  }

  @override
  void onLowLevelReport(data) {
    BotToast.showText(text: data['msg']);
    _startVibrate();
  }

  @override
  void onUsersData(data) {
    var remoteUsers = data['data'];
    var result = remoteUsers
        .map<InGameUserIdentityModel>(
            (json) => InGameUserIdentityModel.fromJson(json))
        .toList() as List<InGameUserIdentityModel>;

    // binding
    for (var element in result) {
      users.add(InGameUserModel(userIdentity: element));
    }
    gameStarted = true;
    update(['users', 'gameStarted']);
  }

  /*====================================GAME EVENT - GAME ACTION======================================*/

  @override
  void onActionEnd() {}

  @override
  void onGameAction(data) async {
    var parse = data['data'];
    parse.map((json) {
      String userId = json['user_id'];
      dynamic userStatus = json['user_status'];
      dynamic userAction = json['user_action'];
      var user = users[users
          .indexWhere((element) => element.userIdentity.user_id == userId)];

      user
        ..connected = userStatus['is_connected']
        ..alive = userStatus['is_alive']
        ..speech = userStatus['is_talking']
        ..vote = userStatus['on_vote']
        ..like = userAction['like']
        ..disLike = userAction['dislike']
        ..challengeRequest = userAction['challenge_request']
        ..acceptChallengeRequest = userAction['accepted_challenge_request']
        ..handRaise = userAction['hand_rise']
        ..speechType = userAction['speech_type']
        ..targetCoverHandRaise = userAction['target_cover_hand_rise']
        ..acceptHandRaise = userAction['target_cover_accepted'];

      update([userId]);

      // update like , dislike to default after 2 sec
      if (user.like || user.disLike) {
        Future.delayed(const Duration(seconds: likeDislikeTimer), () {
          user
            ..like = false
            ..disLike = false;
          update([userId]);
        });
      }

      // hand raise
      if (user.handRaise) {
        Future.delayed(const Duration(seconds: handRaiseTimer), () {
          user.handRaise = false;
          update([userId]);
        });
      }

      // challenge request or accept challenge request
      if (user.challengeRequest || user.acceptChallengeRequest) {
        Future.delayed(
            const Duration(
                seconds: challengeRequestOrAcceptChallengeRequestTimer), () {
          user.challengeRequest = false;
          user.acceptChallengeRequest = false;
          update([userId]);
        });
      }

      // on vote
      if (user.vote) {
        Future.delayed(const Duration(seconds: onVoteUserTimer), () {
          user.vote = false;
          update([userId]);
        });
      }

      // announce speech time
      if (user.speech && user.userIdentity.user_id == myUserId) {
        // vibrate
        _startVibrate();

        // msg
        BotToast.showText(
          text: 'نوبت صحبت شماست',
          duration: const Duration(seconds: 3),
        );
      }

      // target cover
      if (user.targetCoverHandRaise || user.acceptHandRaise) {
        Future.delayed(const Duration(seconds: targetCoverHandRaise), () {
          user.targetCoverHandRaise = false;
          user.acceptHandRaise = false;
          update([user.userIdentity.user_id]);
        });
      }
    }).toList();
  }

  @override
  void onGameEvent(data) async {
    String event = data['data']['game_event'];
    _modifyGameEvent(event);
  }

  void _modifyGameEvent(String event) async {
    if (event == 'end') {
      gameEvent = 'none';
      update(['actionBoard']);
      return;
    }

    if (event == 'day') {
      if (Get.rawRoute?.settings.name == 'nightIdle') {
        Get.back();
      }
    }

    if (event == 'night') {
      update(['actionBoard']);
    }

    // main game event
    if (event == 'day' ||
        event == 'night' ||
        event == 'vote' ||
        event == 'chaos' ||
        event == 'end') {
      mainGameEvent = event;
      update(['mainGameEvent']);
      // disable day gun status by changing game event
      if (mainGameEvent == 'night' ||
          mainGameEvent == 'chaos' ||
          mainGameEvent == 'vote') {
        dayGunActive = false;
        dayGunAvailable = false;
        update(['dayGunStatus']);
      }
    }

    gameEvent = event == 'vote' ? 'none' : event;

    if (event == 'chaos') {
      if (Get.rawRoute?.settings.name == 'nightIdle') {
        Get.back();
      }
    }

    if (users
        .singleWhere((element) => element.userIdentity.user_id == myUserId)
        .alive) {
      update(['actionBoard']);
    } else {
      gameEvent = 'dead';
      update(['actionBoard']);
    }
  }

  /*=====================================DAY SPEECH=====================================*/
  @override
  void onSpeechTimeUp(data) {
    var user = data['data']['user_id'];
    if (user == myUserId) {
      _liveKit.participantSpeaking(_gameRoom, false);
      micToggle.value = false;
      gameEvent = 'none';
      update(['actionBoard']);
    }
  }

  @override
  void onStartSpeech() {
    if (myUserId != null) {
      micToggle.value = true;
      _liveKit.participantSpeaking(_gameRoom, micToggle.value);
      gameEvent = 'speech';
      update([myUserId!, 'actionBoard']);
    }
  }

  @override
  void onCurrentPlayerTalking(data) {
    // update challenge request
    activeChallengeRequestTimer.value = false;
    var currentUserId = data['current'];
    var time = data['timer'];
    if (currentUserId == myUserId) {
      gameEvent = 'speech';
    }
    users
        .firstWhere((element) => element.userIdentity.user_id == currentUserId)
        .speech = true;

    update([currentUserId, 'actionBoard', 'timer']);
    // timer
    _startGameTimer(time);
  }

  @override
  void onCurrentPlayerTalkingEnd(data) {
    _stopGameTimer();

    for (var element in users) {
      element.speech = false;
      update([element.userIdentity.user_id]);
    }
  }

  /*=====================================DAY GUN=====================================*/
  @override
  void onReportGun() {
    BotToast.showCustomNotification(
      toastBuilder: (cancelFunc) {
        return const AnnounceNotificationMessage(
            msg: 'تفنگدار بهت تیر داده ، روز بعد میتوی از اسلحت استفاده کنی',
            img: 'images/rifleman.png');
      },
      duration: const Duration(seconds: 5),
    );
    _startVibrate();
  }

  @override
  void onDayGunStatus(data) {
    var parse = data['data'];
    bool status = parse['gun_enable'] as bool;
    dayGunAvailable = status;
    update(['dayGunStatus']);
  }

  @override
  void onDayUsedGun(data) async {
    var parse = data['data'];
    String fromUser = parse['from_user'];
    String toUser = parse['to_user'];
    String kind = parse['kind'];

    // vibrate
    _startVibrate();

    InGameUserModel from = users
        .singleWhere((element) => element.userIdentity.user_id == fromUser);
    from.shot = true;
    update([from.userIdentity.user_id]);

    await Future.delayed(const Duration(milliseconds: 1500), () {
      from.shot = false;
      update([from.userIdentity.user_id]);
    });

    // shot sound
    _audioPlayer.play(AssetSource('pistol_sound.mp3'));

    InGameUserModel to =
        users.singleWhere((element) => element.userIdentity.user_id == toUser);
    to.targeted = true;
    update([to.userIdentity.user_id]);

    await Future.delayed(const Duration(seconds: 3), () {
      to.targeted = false;
      update([to.userIdentity.user_id]);
    });

    to.targetedType = kind;
    update([to.userIdentity.user_id]);

    Future.delayed(const Duration(seconds: 2), () {
      to.targetedType = 'not';
      update([to.userIdentity.user_id]);
    });
  }

  /*=====================================CHALLENGE===================================*/
  @override
  void onAcceptChallenge() {
    showCustomSnack(
        context: Get.context!,
        title: 'چالش',
        body: 'درخواست چالش شما تایید شد');
  }

  @override
  void onUsersChallengeStatus(data) {
    var parse = data['data'];
    parse.map((json) {
      var user = users.firstWhereOrNull(
          (element) => element.userIdentity.user_id == json['user_id']);
      if (user != null) {
        user.canTakeChallenge = json['status'];
        if (user.userIdentity.user_id == myUserId) {
          update([user.userIdentity.user_id, 'challengeRequestCountDown']);
        } else {
          update([user.userIdentity.user_id]);
        }
      }
    }).toList();
  }

  /*=======================================TARGET COVER===================================*/
  @override
  void onGrantPermission(data) {
    var grant = data['grant'];
    targetCoverPermission = grant;
  }

  @override
  void onBecomeVolunteer(data) async {
    var parse = data['data'];
    String requesterId = parse['requester_id'];
    String option = parse['option'];
    var timer = parse['timer'];
    late String type;
    switch (option) {
      case 'target':
        type = 'تارگت';
        break;
      case 'cover':
        type = 'کاور';
        break;
      case 'about':
        type = 'درباره';
        break;
      default:
    }
    InGameUserModel? user = users.firstWhereOrNull(
        (element) => element.userIdentity.user_id == requesterId);

    BotToast.showCustomNotification(
        toastBuilder: (cancelFunc) => InGameMsgDialog(
              msg: 'درخواست $type داره',
              user: user,
            ),
        duration: Duration(seconds: timer),
        align: Alignment.center);

    await Future.delayed(Duration(seconds: timer), () {
      gameEvent = 'vote';
      update(['actionBoard']);
    });
  }

  @override
  void onUsingSpeechOptions(data) {
    var parse = data['data'];
    int timer = parse['timer'];

    // show dialog
    Get.dialog(
        RequestSpeechOptions(
          timer: timer,
          accepted: () {
            // close dialog
            if (Get.isDialogOpen == true) {
              Get.back();
            }

            _usingSpeechOptions(true);
          },
          denied: () {
            // close dialog
            if (Get.isDialogOpen == true) {
              Get.back();
            }

            _usingSpeechOptions(false);
          },
        ),
        barrierDismissible: false);
  }

  /*=====================================VOTE=====================================*/
  @override
  void onVote(data) {
    dynamic parse = data['data'];
    String? _ = parse['user_id']; // user on vote
    int timer = parse['timer'] as int;
    gameEvent = 'vote';
    update(['actionBoard']);
    Future.delayed(
      Duration(
        seconds: timer,
      ),
      () {
        gameEvent = 'none';
        update(['actionBoard']);
      },
    );
    /* var availableUsers = parse['available_users'];
    if (currentOnVoteUserId != null) {
      /* if (myUserId == currentOnVoteUserId) {
        gameEvent = 'none';
        update(['actionBoard']);
      } else {
        
      } */
      List<String> others = List<String>.from(availableUsers);
        for (var user in others) {
          if (users.firstWhereOrNull((element) =>
                  element.userIdentity.user_id == user && element.alive) !=
              null) {
            gameEvent = 'vote';
            update(['actionBoard']);

            Future.delayed(
              Duration(
                seconds: timer,
              ),
              () {
                gameEvent = 'none';
                update(['actionBoard']);
              },
            );
          }
        }
    } else {
      
    } */
  }

  /*======================================NIGHT====================================*/

  @override
  void onDetectiveInquiryResponse(data) {
    bool inquiry = data['inquiry'] as bool;
    // show dialog
    Get.dialog(DetectiveInquiryResponse(result: inquiry),
        barrierDismissible: false);
  }

  @override
  void onMafiaDecision(data) {
    bool _ = data['nato_availabel'];
    int timer = data['timer'];

    if (Get.isDialogOpen == true) {
      Get.back();
    }

    Get.dialog(MafiaDecision(
      timer: timer,
      onCallback: (onNato, onShot) {
        // check
        if (Get.isDialogOpen == true) {
          Get.back();
        }
        // night
        gameEvent = 'night';
        update(['actionBoard']);

        Map<String, dynamic> body = {
          'shot': onShot,
          'nato': onNato,
          'role': serverCharacter
        };
        Map<String, dynamic> json = {'op': 'mafia_decision', 'data': body};
        _socketManager.getNatoGameSocket.emit('game_handle', json);
      },
      onTimeUp: () {
        // check
        if (Get.isDialogOpen == true) {
          Get.back();
        }

        // night
        gameEvent = 'night';
        update(['actionBoard']);
      },
    ));
  }

  @override
  void onMafiaShot(data) async {
    _startVibrate();
    _audioPlayer.play(AssetSource('act_time.mp3'));
    List<dynamic> availableUsers = data['availabel_users'];
    int maxCount = data['max'];
    int timer = data['timer'];

    // disable night idle
    if (Get.isBottomSheetOpen ?? false) {
      Get.back();
    }
    // navigate to night act widget
    await Future.delayed(const Duration(seconds: 1), () {});
    Get.to(
      () => NightActTime(
        availableUses: availableUsers,
        users: users,
        maxCount: maxCount,
        timer: timer,
        serverCharacter: 'godfather',
        isMafiaShot: true,
        controller: this,
        onTimeUp: () async {
          // close any bottom sheet
          if (Get.isBottomSheetOpen ?? false) {
            Get.back();
          }
          // navigate to game screen
          Get.until(
              (route) => Get.currentRoute == _router.getRoute('nato_game'));

          Future.delayed(const Duration(seconds: 1), () {
            // show night idle
            // _showNightIdle();
            gameEvent = 'night';
            update(['actionBoard']);
          });
        },
        onSelectedUsersCallback: (selectedUsers) async {
          // close any bottom sheet
          if (Get.isBottomSheetOpen ?? false) {
            Get.back();
          }
          // play shot sound
          _audioPlayer.play(AssetSource('pistol_sound.mp3'));
          // navigate to game screen
          Get.until(
              (route) => Get.currentRoute == _router.getRoute('nato_game'));
          // send night act to server
          _sendNightAct(selectedUsers, true);

          Future.delayed(const Duration(seconds: 1), () {
            // show night idle
            // _showNightIdle();
            gameEvent = 'night';
            update(['actionBoard']);
          });
        },
      ),
      duration: const Duration(milliseconds: 800),
    );
  }

  @override
  void onMafiaSpeech(data) async {
    nextPlayerAvailable = false;
    var token = data['token'];
    var timer = data['timer'];
    var teammate = data['teammate'];

    // dismiss night dialog
    Future.delayed(const Duration(milliseconds: 200), () {
      _hideNightIdle();
    });

    _liveKit.connectToRoom(_gameRoom, token);

    users
        .singleWhere((element) => element.userIdentity.user_id == teammate)
        .speech = true;

    gameEvent = 'speech';
    update(['actionBoard', teammate, 'btnNextPlayer']);
    _startGameTimer(timer);
  }

  @override
  void onMafiaSpeechEnd() {
    nextPlayerAvailable = true;

    gameEvent = 'night';
    for (var element in users) {
      element.speech = false;
      update([element.userIdentity.user_id]);
    }
    update(['actionBoard', 'btnNextPlayer']);

    // night idle
    // _showNightIdle();
    micToggle.value = false;
    _liveKit.participantSpeaking(_gameRoom, false);
    _liveKit.disConnectRoom(_gameRoom);
  }

  @override
  void onMafiaVisitation(data) {
    var parse = data['data']['mafia'];
    var dec = encryptDecrypt(parse.toString());
    var json = jsonDecode(dec);
    var list = json
        .map<MafiaVisitationModel>(
            (model) => MafiaVisitationModel.fromJson(model, 'nato'))
        .toList() as List<MafiaVisitationModel>;

    mafiaList.addAll(list);
    // mafia visitation
    Get.bottomSheet(MafiaVisitation(mafiaList: mafiaList, users: users),
        isDismissible: false);

    for (var mafia in mafiaList) {
      users
          .singleWhere(
              (element) => element.userIdentity.user_id == mafia.userId)
          .mafiaCharacterImg = mafia.characterImage;
    }

    update(['mafiaList']);
  }

  @override
  void onUseNightAbility(data) async {
    var parse = data['data'];
    int maxCount = parse['max_count'] as int;
    List<dynamic> availableUsers = parse['availabel_users'];
    bool canAct = parse['can_act'] as bool;
    int timer = parse['timer'];
    String msg = parse['msg'];
    if (!canAct) {
      showCustomSnack(
        context: Get.context!,
        title: 'دینگ دینگ',
        body: msg,
        duration: const Duration(seconds: 6),
      );

      _startVibrate();
      return;
    }

    _startVibrate();
    _audioPlayer.play(AssetSource('act_time.mp3'));

    // disable night idle
    if (Get.isBottomSheetOpen ?? false) {
      Get.back();
    }

    // navigate to night act widget
    await Future.delayed(const Duration(seconds: 1), () {});
    Get.to(
      () => NightActTime(
        availableUses: availableUsers,
        users: users,
        maxCount: maxCount,
        timer: timer,
        serverCharacter: serverCharacter,
        controller: this,
        isMafiaShot: false,
        onTimeUp: () async {
          // close any bs
          if (Get.isBottomSheetOpen ?? false) {
            Get.back();
          }

          // navigate to game screen
          Get.until(
              (route) => Get.currentRoute == _router.getRoute('nato_game'));

          Future.delayed(const Duration(seconds: 1), () {
            // show night idle
            // _showNightIdle();
            gameEvent = 'night';
            update(['actionBoard']);
          });
        },
        onSelectedUsersCallback: (selectedUsers) async {
          // close any bottom sheet
          if (Get.isBottomSheetOpen ?? false) {
            Get.back();
          }
          // navigate to game screen
          Get.until(
              (route) => Get.currentRoute == _router.getRoute('nato_game'));
          // send night act to server
          _sendNightAct(selectedUsers, false);

          Future.delayed(const Duration(seconds: 1), () {
            // show night idle
            // _showNightIdle();
            gameEvent = 'night';
            update(['actionBoard']);
          });
        },
      ),
      duration: const Duration(milliseconds: 800),
    );
  }

  /*====================================CHAOS======================================*/

  /// chaos vote
  void chaosVote(String userId) {
    Map<String, dynamic> data = {'user_id': userId};
    Map<String, dynamic> op = {'op': 'chaos_vote', 'data': data};

    _socketManager.getNatoGameSocket.emit('game_handle', op);

    // disable any other hands
    users.where((element) => element.alive).forEach((element) {
      element.availableHandShake = false;
      update([element.userIdentity.user_id]);
    });
  }

  @override
  void onChaosAllSpeech(data) {
    int timer = data['timer'] as int;

    var aliveUsers = users.where((element) => element.alive).toList();

    for (var element in aliveUsers) {
      element.speech = true;
      update([element.userIdentity.user_id]);
      micToggle.value = true;

      _liveKit.participantSpeaking(_gameRoom, micToggle.value);
    }
    Future.delayed(const Duration(milliseconds: 100), () {
      gameEvent = 'speech';
      nextPlayerAvailable = false;
      update(['actionBoard', 'btnNextPlayer']);
    });
    _startGameTimer(timer);
  }

  @override
  void onChaosAllSpeechEnd() {
    micToggle.value = false;
    _liveKit.participantSpeaking(_gameRoom, false);
    var aliveUsers = users.where((element) => element.alive).toList();

    for (var element in aliveUsers) {
      element.speech = false;
      update([element.userIdentity.user_id]);
    }
    gameEvent = 'none';
    nextPlayerAvailable = true;
    update(['actionBoard', 'btnNextPlayer']);
  }

  @override
  void onChaosLastDecision(data) {
    var parse = data['data'];
    var timer = parse['timer'] as int;

    //clear previous vote records
    users.where((element) => element.alive).toList().forEach((element) {
      element.handShakeTo = null;
      update([element.userIdentity.user_id]);
    });

    // announcement
    BotToast.showText(
      text: 'شما تایید کننده ای ، با کیی دست میدی ؟',
      duration: const Duration(seconds: 3),
    );

    var availableUsers =
        parse['available_users'].map((e) => e.toString()).toList();

    users
        .where((user) =>
            user.alive && availableUsers.contains(user.userIdentity.user_id))
        .toList()
        .forEach((element) {
      element.availableHandShake = true;
      update([element.userIdentity.user_id]);
    });

    // clear data
    _chaosTimerManagement(timer);

    Future.delayed(Duration(seconds: timer), () {
      if (_chaosTimer?.isActive ?? false) {
        users
            .where((user) =>
                user.alive &&
                availableUsers.contains(user.userIdentity.user_id))
            .toList()
            .forEach((element) {
          element.availableHandShake = false;
          update([element.userIdentity.user_id]);
        });
      }
    });

    _startGameTimer(timer);
  }

  @override
  void onChaosSpeechUser(data) {
    // clear previous vote records
    users.where((element) => element.alive).toList().forEach((element) {
      element.handShakeTo = null;
      update([element.userIdentity.user_id]);
    });

    nextPlayerAvailable = true;
    gameEvent = 'speech';
    update(['actionBoard', 'btnNextPlayer']);
  }

  @override
  void onChaosTurnToHandShake(data) {
    var parse = data['data'];
    int timer = parse['timer'];
    String? userId = parse['user_id'] as String?;
    if (userId == null) {
      users.where((element) => element.alive).toList().forEach((element) {
        element.turnToHandShake = false;
        update([element.userIdentity.user_id]);
      });

      users.where((element) => element.alive).toList().forEach((element) {
        element.availableHandShake = false;
        update([element.userIdentity.user_id]);
      });
      // timer
      _chaosTimer?.cancel();
    } else {
      users.where((element) => element.alive).toList().forEach((element) {
        if (element.userIdentity.user_id == userId) {
          element.turnToHandShake = true;
          update([element.userIdentity.user_id]);
        } else {
          element.turnToHandShake = false;
          update([element.userIdentity.user_id]);
        }
      });
    }

    if (userId != null) {
      _startGameTimer(timer);
    }
  }

  @override
  void onChaosVote(data) {
    var parse = data['data'];
    int timer = data['timer'] as int;

    BotToast.showText(
      text: 'به یکی از بازیکنا دست بده',
      duration: const Duration(seconds: 3),
    );
    _startVibrate();

    var availableUsers =
        parse['available_users'].map((e) => e.toString()).toList();

    users
        .where((user) =>
            user.alive && availableUsers.contains(user.userIdentity.user_id))
        .toList()
        .forEach((element) {
      element.availableHandShake = true;
      update([element.userIdentity.user_id]);
    });

    _startGameTimer(timer);
  }

  @override
  void onChaosVoteResult(data) {
    var parse = data['data'];
    String from = parse['from_user'];
    String to = parse['to_user'];

    var fromUser = users.singleWhere(
        (element) => element.alive && element.userIdentity.user_id == from);

    var toUser = users.singleWhere(
        (element) => element.alive && element.userIdentity.user_id == to);

    fromUser.handShakeTo = toUser;
    update([fromUser.userIdentity.user_id]);
  }

  @override
  void onClearChaosRecord() {
    //clear previous vote records
    users.where((element) => element.alive).toList().forEach((element) {
      element.handShakeTo = null;
      update([element.userIdentity.user_id]);
    });
  }

  void _chaosTimerManagement(int time) {
    var timeLeft = time;
    _chaosTimer = Timer.periodic(Duration(seconds: time), (timer) {
      if (timeLeft > 0) {
        timeLeft--;
      } else {
        timer.cancel();
        _chaosTimer?.cancel();
      }
    });
  }

  /*====================================== END ====================================*/

  @override
  void onAbandon() {
    _exitFromGame(customExit: false);
  }

  @override
  void onEndGameResult(data) {
    // play sound
    _audioPlayer.play(AssetSource('victory_sound.mp3'));
    _startVibrate();
    var parse = data['data'];
    var users = parse['users'];
    var winner = parse['winner'] as String;
    parse['scenario'] as String; // scenario
    parse['free_speech_timer'] as dynamic; // could be double // speech timer

    var endGameUsers = users
        .map<EndGameResultModel>((json) => EndGameResultModel.fromJson(json))
        .toList() as List<EndGameResultModel>;

    // show dialog
    Future.delayed(const Duration(milliseconds: 1500), () {
      Get.dialog(
          EndGameDialog(
            mafia: winner == 'mafia',
            callback: (exit, leaderBoard) async {
              if (exit) {
                if (Get.isDialogOpen ?? false) {
                  Get.back();
                }

                if (Get.isBottomSheetOpen ?? false) {
                  Get.back();
                }
                // navigate to home
                _exitFromGame(customExit: false);
                return;
              }

              if (leaderBoard) {
                if (Get.isDialogOpen ?? false) {
                  Get.back();
                }

                if (Get.isBottomSheetOpen ?? false) {
                  Get.back();
                }
                // navigate to result page
                Get.toNamed(_router.getRoute('end_game_result'),
                    arguments: {'users': endGameUsers, 'gameId': gameId});
              }
            },
          ),
          barrierDismissible: false);
    });
  }

  @override
  void onInit() {
    super.onInit();
    // init game listeners
    _initGameListeners();

    _getArgs();
  }

  @override
  void onClose() {
    super.onClose();
    _socketManager.getNatoGameSocket.clearSockets();
  }
}
