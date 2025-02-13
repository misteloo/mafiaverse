// ignore_for_file: invalid_use_of_internal_member

import 'dart:async';

import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:livekit_client/livekit_client.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/models/lobby/game/lobby_game_creator_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_event_enum.dart';
import 'package:mafia/models/lobby/game/lobby_game_modified_permission_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_observer_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_over_all_permissions_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_player_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_player_permissions_model.dart';
import 'package:mafia/models/lobby/game/lobby_game_screen_creator_message_model.dart';
import 'package:mafia/screens/lobby/game/lobby_game_screen.dart';
import 'package:mafia/utils/constants/address.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_game_listener.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:mafia/utils/voice/live_kit_manager.dart';

import '../../../models/profile/check_other_profile_model.dart';
import '../../../utils/widget/check_player_profile.dart';

class LobbyGameScreenController extends GetxController
    implements SocketLobbyGameListener {
  // injection
  final SocketManager _socket = Get.find();
  final LiveKitManager _liveKit = Get.find();
  final ApiRepository _api = Get.find();
  final HashRouting _routing = Get.find();

  // instances
  bool isPlayer = false;
  bool isCreator = false;
  bool isObserver = false;
  bool gameStarted = false;
  bool roomListening = false;
  bool moderatorUnreadMessage = false;
  bool moderatorPanelOpen = false;
  int gameEventCount = 0;

  final TextEditingController messageToCreatorController =
      TextEditingController();

  // global keys
  final creatorMessageGlobalKey = GlobalKey<AnimatedListState>();
  final playersActGlobalKey = GlobalKey<AnimatedListState>();

  // timers
  Timer? likeDislikeTimer;

  // obx
  var likeDislikeTimeLeft = 5.obs;
  var micToggle = false.obs;

  // late
  late String userId;
  late String creatorId;
  late String lobbyId;
  late Room _gameRoom;
  late LobbyGameOverAllPermissionsModel lobbyOverAllPermissions;

  // lists
  final List<LobbyGamePlayerModel> players =
      List<LobbyGamePlayerModel>.empty(growable: true);

  final List<LobbyGamePlayerPermissionsModel> playersPermissionList =
      List<LobbyGamePlayerPermissionsModel>.empty(growable: true);

  final List<String> sideList = List<String>.empty(growable: true);

  final List<LobbyGameScreenCreatorMessageModel> creatorMessages =
      List<LobbyGameScreenCreatorMessageModel>.empty(growable: true);

  final List<LobbyGameObserverModel> observers =
      List<LobbyGameObserverModel>.empty(growable: true);

  // act list
  final List<LobbyGamePlayerModel> playerActs =
      List<LobbyGamePlayerModel>.empty(growable: true);

  LobbyGamePlayerPermissionsModel? playerPermissions;
  LobbyGameCreatorModel? creatorModel;
  String? character;
  String? side;
  LobbyGameEvent? gameEvent;

  @override
  void onInit() {
    super.onInit();
    _gameRoom = _liveKit.createRoom;
    // get args
    userId = Get.arguments['user_id'];
    creatorId = Get.arguments['creator_id'];
    isObserver = Get.arguments['is_observer'];
    isPlayer = Get.arguments['is_player'];
    isCreator = Get.arguments['is_creator'];
    lobbyId = Get.arguments['lobby_id'];

    update(['player_type', 'action_panel']);

    // listener
    _socket.getLobbyGameSocket.addListener(this);

    // ready
    _readyToGame(lobbyId);
  }

  @override
  void dispose() {
    if (likeDislikeTimer != null) {
      likeDislikeTimer?.cancel();
    }
    super.dispose();
  }

  @override
  void onClose() async {
    await _liveKit.disConnectRoom(_gameRoom);
    super.onClose();
  }

  // run like dislike timer
  void runLikeDislikeTimer() {
    if (likeDislikeTimer != null) {
      return;
    }
    likeDislikeTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (likeDislikeTimeLeft.value > 0) {
        likeDislikeTimeLeft.value--;
      } else {
        likeDislikeTimeLeft.value = 5;
        likeDislikeTimer = null;
        timer.cancel();
      }
    });
  }

  // ready to game
  void _readyToGame(String lobbyId) {
    var op = {'op': 'ready_to_game', 'lobby_id': lobbyId};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // like
  void like() {
    if (playerPermissions?.like_dislike == false) return;
    if (likeDislikeTimer != null) return;
    var data = {'action': 'like', 'new_status': true, 'auto_turn_off': true};
    var op = {'op': 'user_action', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
    runLikeDislikeTimer();
  }

  // dislike
  void disLike() {
    if (playerPermissions?.like_dislike == false) return;
    if (likeDislikeTimer != null) return;
    var data = {'action': 'dislike', 'new_status': true, 'auto_turn_off': true};
    var op = {'op': 'user_action', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
    runLikeDislikeTimer();
  }

  // challenge
  void challengeRequest(bool state) {
    if (playerPermissions?.challenge == false) return;
    var data = {
      'action': 'challenge',
      'new_status': state,
      'auto_turn_off': false
    };
    var op = {'op': 'user_action', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // hand rise
  void handRise(bool state) {
    if (playerPermissions?.hand_rise == false) return;
    var data = {
      'action': 'hand_rise',
      'new_status': state,
      'auto_turn_off': false
    };
    var op = {'op': 'user_action', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // day act
  void dayAct(bool state) {
    if (playerPermissions?.day_act == false) return;
    var data = {
      'action': 'day_act',
      'new_status': state,
      'auto_turn_off': false
    };
    var op = {'op': 'user_action', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // remove creator unread messages
  void clearCreatorUnreadMessages() {
    moderatorUnreadMessage = false;
    update(['unread_message']);
  }

  // mic
  void _mic(bool state) {
    var data = {
      'action': 'speech',
      'new_status': state,
      'auto_turn_off': false
    };
    var op = {'op': 'user_action', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // self
  LobbyGamePlayerModel selfDetail() {
    return players.singleWhere((element) => element.user_id == userId);
  }

  // check profile
  void checkPlayerProfile(String userId) async {
    Map body = {
      'user_id': userId,
    };
    var response = await _api.checkProfile(body);

    response.fold((left) {
      // update user
      BotToast.showText(text: 'خطا در دریافت اطلاعات');
    }, (right) {
      // update user
      var parse = CheckOtherProfileModel.fromJson(right.data['data']['data']);
      if (Get.isBottomSheetOpen == true) {
        Get.back();
      }
      // show player detail
      Get.bottomSheet(
          backgroundColor: Colors.transparent,
          CheckPlayerProfile(
            profile: parse,
          ));
    });
  }

// speech
  void speechToggle() {
    if (isCreator) {
      micToggle.toggle();
      _liveKit.participantSpeaking(_gameRoom, micToggle.value);
      _mic(micToggle.value);
    } else {
      if (playerPermissions?.speech == false) return;
      micToggle.toggle();
      _liveKit.participantSpeaking(_gameRoom, micToggle.value);
      // update mic status of player
      _mic(micToggle.value);
    }
  }

  // after played died disable actions or permissions like mic
  void _disablePlayerRunTimes() {
    micToggle(false);
    _liveKit.participantSpeaking(_gameRoom, false);
  }

  // change specific player permission
  void changePlayerPermission(
      String userId, List<LobbyGameModifiedPermissionModel> permissions) {
    var objects = permissions
        .map((e) => {'permission': e.name, 'status': e.active})
        .toList();
    Map data = {'user_id': userId, 'permissions': objects};
    Map op = {'op': 'change_multi_permission', 'data': data};

    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // change player alive status
  void setPlayerAliveStatus(String userId, bool status) {
    Map data = {
      'target_player': userId,
      'selected_status': 'alive',
      'new_value': status
    };
    Map op = {'op': 'change_player_status', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // tell server we need room status
  void roomOverAll() {
    Map op = {'op': 'permissions_overall'};
    _socket.emit('custom_game_handler', op);
  }

  // create private speech
  void createPrivateSpeechList(List<String> name) {
    if (micToggle.value) {
      micToggle.toggle();
    }
    update(['private_speech_loading']);
    Map data = {'target_players': name};
    Map op = {'op': 'create_private_speech', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // disable private speech
  void disablePrivateSpeech() {
    Map op = {'op': 'end_private_speech'};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // send message to creator
  void sendMessageToCreator() {
    Map data = {'content': messageToCreatorController.text};
    Map op = {'op': 'send_message_to_creator', 'data': data};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);

    messageToCreatorController.clear();
  }

  // leave game by moderator
  void leaveGame() async {
    Map op = {'op': 'left'};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
    await _socket.getLobbyGameSocket.clearListeners();
    _liveKit.participantSpeaking(_gameRoom, false);
    await _liveKit.disConnectRoom(_gameRoom);
    await _liveKit.dispose(_gameRoom);
    Get.until((route) =>
        Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
  }

  // dc
  void dc() async {
    Map op = {'op': 'dc'};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
    await _socket.getLobbyGameSocket.clearListeners();
    _liveKit.participantSpeaking(_gameRoom, false);
    await _liveKit.disConnectRoom(_gameRoom);
    await _liveKit.dispose(_gameRoom);
    Get.until((route) =>
        Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
  }

  // change side
  void changePlayerSide(String id, String side) {
    Map data = {
      'target_player': id,
      'selected_status': 'side',
      'new_value': side
    };

    Map op = {'op': 'change_player_status', 'data': data};

    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  // end game
  void endGame() async {
    Map op = {'op': 'end_game'};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);

    await _socket.getLobbyGameSocket.clearListeners();
    _liveKit.participantSpeaking(_gameRoom, false);
    await _liveKit.disConnectRoom(_gameRoom);
    await _liveKit.dispose(_gameRoom);
    Get.until((route) =>
        Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
  }

  // last move card
  void pickLastMoveCard() {
    Map op = {'op': 'pick_last_move'};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);
  }

  @override
  void onLivekitToken(data) async {
    await _liveKit.connectToRoom(_gameRoom, data['token']);
    gameStarted = true;
    update(['game_started']);
  }

  @override
  void onPermissionsStatus(data) {
    playerPermissions =
        LobbyGamePlayerPermissionsModel.fromJson(data['permissions']);
    if (playerPermissions?.speech == false) {
      _disablePlayerRunTimes();
    }

    if (playerPermissions?.hand_rise == false) {
      handRise(false);
    }
    if (playerPermissions?.challenge == false) {
      challengeRequest(false);
    }
    update(['player_permissions']);
  }

  @override
  void onAllPlayersStatus(data) {
    var list = data['players_status']
        .map<LobbyGamePlayerModel>(
            (json) => LobbyGamePlayerModel.fromJson(json))
        .toList() as List<LobbyGamePlayerModel>;

    players.clear();

    for (var element in list) {
      players.add(element);
      // show character to player
      if (element.user_id == userId) {
        character = element.character;
        side = element.side;

        // show bs
        Get.bottomSheet(
          ShowCharacterToUser(character: character!, side: side!),
          backgroundColor: Colors.transparent,
        );
      }

      update(['players', element.user_id, 'player_character']);
    }
  }

  @override
  void onCreatorStatus(data) {
    creatorModel = LobbyGameCreatorModel.fromJson(data['creator_status']);
    update(['creator']);
  }

  @override
  void onPlayerStatusUpdate(data) {
    var userId = data['user_id'];

    var user = players.firstWhereOrNull(
      (element) => element.user_id == userId,
    );
    if (user != null) {
      var alive = data['alive'];
      var connected = data['connected'];
      var speech = data['speech'];
      var handRise = data['hand_rise'];
      var dayAct = data['day_act'];
      var like = data['like'];
      var disLike = data['dislike'];
      var challenge = data['challenge'];
      var challengeAccepted = data['challenge_accepted'];
      var private = data['private'];

      // update status
      user.status
        ..alive = alive
        ..connected = connected
        ..speech = speech
        ..hand_rise = handRise
        ..day_act = dayAct
        ..like = like
        ..dislike = disLike
        ..challenge = challenge
        ..challenge_accepted = challengeAccepted
        ..private = private;

      if (isPlayer) {
        // update ui
        if (userId == selfDetail().user_id) {
          // character and side
          side = data['side'];
          user.side = side!;
          update([
            'player_character',
          ]);
        }
      }

      update([
        user.user_id,
        'players_act',
        'players',
      ]);
      // update player acts
      var findPlayer = playerActs
          .firstWhereOrNull((element) => element.user_id == user.user_id);
      if (findPlayer == null) {
        if (user.status.like ||
            user.status.dislike ||
            user.status.challenge ||
            user.status.day_act ||
            user.status.hand_rise) {
          playerActs.insert(0, user);
          playersActGlobalKey.currentState?.insertItem(0);
        }
      } else {
        int index =
            playerActs.indexWhere((element) => element.user_id == user.user_id);
        var inAct = user.status;
        // hand rise
        if (!inAct.like &&
            !inAct.dislike &&
            !inAct.challenge &&
            !inAct.hand_rise &&
            !inAct.day_act) {
          playersActGlobalKey.currentState?.removeItem(
              index,
              (context, animation) => PlayerActs.animatedListItem(
                  playerActs[index], animation, context),
              duration: const Duration(milliseconds: 100));

          Future.delayed(const Duration(milliseconds: 100), () {
            playerActs.removeAt(index);
          });
        }
      }

      if (like || disLike) {
        Future.delayed(
          const Duration(seconds: 2),
          () {
            user.status
              ..like = false
              ..dislike = false;
            update([
              user.user_id,
            ]);
            int index = playerActs
                .indexWhere((element) => element.user_id == user.user_id);

            if (index != -1) {
              if (!user.status.challenge &&
                  !user.status.hand_rise &&
                  !user.status.day_act) {
                playersActGlobalKey.currentState?.removeItem(
                  index,
                  (context, animation) => PlayerActs.animatedListItem(
                      playerActs[index], animation, context),
                  duration: const Duration(milliseconds: 100),
                );

                Future.delayed(const Duration(milliseconds: 100), () {
                  playerActs.removeAt(index);
                });
              }
            }
          },
        );
      }
    }
  }

  @override
  void onPlayersPermission(data) {
    var list = data['players_permission']
        .map<LobbyGamePlayerPermissionsModel>(
            (json) => LobbyGamePlayerPermissionsModel.fromJson(json))
        .toList();

    playersPermissionList.clear();
    playersPermissionList.addAll(list);
  }

  @override
  void onReport(data) {
    var msg = data['msg'];
    var timer = data['timer'];
    // show dialog msg
    BotToast.showCustomNotification(
      toastBuilder: (cancelFunc) {
        return InGameMsgDialog(
          msg: msg,
        );
      },
      duration: Duration(seconds: timer),
      align: Alignment.center,
    );
  }

  @override
  void onGameEvent(data) {
    switch (data['game_event']) {
      case 'روز':
        gameEvent = LobbyGameEvent.day;
        update(['game_event']);
        break;
      case 'رای گیری':
        gameEvent = LobbyGameEvent.vote;
        update(['game_event']);
        break;
      case 'شب':
        gameEvent = LobbyGameEvent.night;
        update(['game_event']);
        break;
      case 'کی آس':
        gameEvent = LobbyGameEvent.chaos;
        update(['game_event']);
        break;
    }
  }

  @override
  void onPermissionsOverAllStatus(data) {
    lobbyOverAllPermissions =
        LobbyGameOverAllPermissionsModel.fromJson(data['overall_status']);
    update(['over_all_permissions']);
    if (Get.isBottomSheetOpen ?? false) {
      Get.back();
    }
    Get.bottomSheet(
        ExpandedMainMenuForCreator(
          overAll: lobbyOverAllPermissions,
          gameEvent: gameEvent == LobbyGameEvent.day
              ? 'روز'
              : gameEvent == LobbyGameEvent.vote
                  ? 'رای گیری'
                  : gameEvent == LobbyGameEvent.night
                      ? 'شب'
                      : 'کی آس',
          callbackModifiedPermissions: (permissionsList, gameEvent) {
            // close bs
            Get.back();
            if (gameEvent != null) {
              // change game event
              var data = {'new_game_event': gameEvent};
              var op = {'op': 'change_game_event', 'data': data};
              _socket.getLobbyGameSocket.emit('custom_game_handler', op);
            }
            if (permissionsList.isNotEmpty) {
              var data = permissionsList
                  .map((e) => {'permission': e.name, 'new_status': e.state})
                  .toList();

              var op = {'op': 'change_all_users_permissions', 'data': data};
              _socket.getLobbyGameSocket.emit('custom_game_handler', op);
            }
          },
        ),
        isScrollControlled: true);
  }

  @override
  void onCreatorMessageBox(data) {
    Future.delayed(const Duration(seconds: 1), () {
      var messages =
          data['messages'].map<LobbyGameScreenCreatorMessageModel>((json) {
        var currentPlayer = players.singleWhere(
          (element) => element.user_id == json['sender'],
        );
        var content = json['content'];
        return LobbyGameScreenCreatorMessageModel(
            player: currentPlayer, content: content);
      }).toList() as List<LobbyGameScreenCreatorMessageModel>;

      creatorMessages.addAll(messages);
      if (creatorMessages.isNotEmpty) {
        if (moderatorPanelOpen == false) {
          moderatorUnreadMessage = true;
        } else {
          moderatorUnreadMessage = false;
        }
      }

      update(['creator_message', 'unread_message']);
    });
  }

  @override
  void onTotalSides(data) {
    var list = data['sides'];
    sideList.clear();
    sideList.addAll(List<String>.from(list));
  }

  @override
  void onPrivateSpeechList(data) async {
    update(['private_speech_loading']);
    BotToast.showText(text: 'شما وارد گفتوگوی خصوصی شدید');
    if (isPlayer) {
      List<String> playersId = List<String>.from(data['players_list']);
      List<LobbyGamePlayerModel> list = playersId
          .map((e) => players.singleWhere((element) => element.user_id == e))
          .toList();

      // show bs
      Get.bottomSheet(
        backgroundColor: Colors.transparent,
        isScrollControlled: true,
        enableDrag: false,
        isDismissible: false,
        PlayerPrivateRoom(
          players: list,
        ),
      );

      Future.delayed(const Duration(seconds: 2), () {
        BotToast.showText(text: 'میکروفون شما روشن شد');
        _liveKit.participantSpeaking(_gameRoom, true);
      });
    }
  }

  @override
  void onPrivateSpeechEnd() {
    BotToast.showText(text: 'گفتوگو به پایان رسید');
    micToggle(false);
    // mic
    _liveKit.participantSpeaking(_gameRoom, false);

    if (isPlayer && Get.isBottomSheetOpen == true) {
      Get.back();
    }
  }

  @override
  void onCreatorNewMessage(data) {
    // update ui
    if (moderatorPanelOpen == false) {
      moderatorUnreadMessage = true;
    } else {
      moderatorUnreadMessage = false;
    }

    var sender = data['new_message']['sender'];
    var content = data['new_message']['content'];

    var currentPlayer =
        players.singleWhere((element) => element.user_id == sender);

    creatorMessages.insert(
        0,
        LobbyGameScreenCreatorMessageModel(
            player: currentPlayer, content: content));
    update(['creator_message', 'unread_message']);
    creatorMessageGlobalKey.currentState
        ?.insertItem(0, duration: const Duration(milliseconds: 250));
  }

  // private speech
  @override
  void onLobbyNewSpeechToken(data) async {
    var token = data['token'];

    await _gameRoom.disconnect();
    _gameRoom.connect(kitUrl, token);
  }

  @override
  void onEndGame() async {
    await _socket.getLobbyGameSocket.clearListeners();
    _liveKit.participantSpeaking(_gameRoom, false);
    await _liveKit.disConnectRoom(_gameRoom);
    await _liveKit.dispose(_gameRoom);
    // prevent to bug from server
    Map op = {'op': 'left'};
    _socket.getLobbyGameSocket.emit('custom_game_handler', op);

    Get.bottomSheet(backgroundColor: Colors.transparent, EndGameResult(
      endCallback: (rate) async {
        if (isObserver) {
          Get.until((route) =>
              Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
        } else {
          Map body = {'rate': rate, 'creator_id': creatorId};
          var response = await _api.rateCreator(body);
          response.fold((left) {
            BotToast.showText(text: 'خطا در ارسال امتیاز');
            Get.until((route) =>
                Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
          }, (right) {
            BotToast.showText(text: right.data['msg']);
            Get.until((route) =>
                Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
          });
        }
      },
    ), isScrollControlled: true, enableDrag: false, isDismissible: false);
  }

  @override
  void onObserversList(data) {
    var obs = data
        .map<LobbyGameObserverModel>(
            (json) => LobbyGameObserverModel.fromJson(json))
        .toList();
    observers.clear();
    observers.addAll(obs);
    update(['observer_count']);
  }

  @override
  void onGameEventCount(data) {
    gameEventCount = data['day'] as int;
    update(['gameEventCount']);
  }
}
