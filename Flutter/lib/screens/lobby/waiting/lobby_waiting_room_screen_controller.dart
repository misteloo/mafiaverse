import 'package:audioplayers/audioplayers.dart';
import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:livekit_client/livekit_client.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/lobby/lobby_detail_model.dart';
import 'package:mafia/models/lobby/waiting_lobby_model.dart';
import 'package:mafia/models/lobby/waiting_lobby_speech_model.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_waiting_listener.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:mafia/utils/voice/live_kit_manager.dart';

import '../../../models/profile/check_other_profile_model.dart';

class LobbyWaitingRoomScreenController extends GetxController
    implements SocketWaitingLobbyListener {
  // injection
  final AudioPlayer _audioPlayer = Get.find();
  final SocketManager _socket = Get.find();
  final SharedPrefManager _sharedPrefManager = Get.find();
  final ApiRepository _api = Get.find();
  final HashRouting _hashRouting = Get.find();
  final LiveKitManager _liveKit = Get.find();

  // instances
  TextEditingController textEditingController = TextEditingController();
  final List<LobbyWaitingMessageModel> chatList =
      List<LobbyWaitingMessageModel>.empty(growable: true);
  // players
  final List<LobbyDetailPlayerModel> playersData =
      List<LobbyDetailPlayerModel>.empty(growable: true);
  // speech
  final List<LobbyWaitingSpeechModel> speechList =
      List<LobbyWaitingSpeechModel>.empty(growable: true);

  // creator
  LobbyDetailPlayerModel? creatorData;

  // players animated list key
  var playersGlobalKey = GlobalKey<AnimatedListState>();

  var chatListKey = GlobalKey<AnimatedListState>();

  // rx
  var chatSound = true.obs;
  var isCreator = false.obs;
  var micToggleStatus = false.obs;

  // late

  late String myUserId;
  late String lobbyId;
  late Room _room;

  // mic toggle
  void micToggle() {
    micToggleStatus.toggle();
    _liveKit.participantSpeaking(_room, micToggleStatus.value);
    Map body = {'status': micToggleStatus.value};
    _socket.emit('waiting_speech', body);

    BotToast.showText(
        text:
            micToggleStatus.value ? 'میکروفون روشن شد' : ' میکروفون خاموش شد');
  }

  void chat() {
    _sendMessage();
  }

  // check profile
  void checkProfile({
    required String userId,
    required Function(CheckOtherProfileModel) profileCallback,
  }) async {
    Map body = {'user_id': userId};

    var response = await _api.checkProfile(body);

    response.fold((left) {
      BotToast.showText(text: 'خطا در دریافت اطلاعات');
    }, (right) {
      var parse = CheckOtherProfileModel.fromJson(right.data['data']['data']);
      profileCallback(parse);
    });
  }

  //start game
  void startGame() {
    _socket.emit('start_custom_game', {'lobby_id': lobbyId});
  }

  // leave lobby
  Future<void> leaveLobby() async {
    // disable mic
    await _room.localParticipant?.setMicrophoneEnabled(false);
    if (isCreator.value) {
      await _socket.getWaitingLobbySocket.clearSockets();
      await _liveKit.disConnectRoom(_room);
      await _room.dispose();
      deleteLobby();
      Get.back();
      return;
    }
    _socket.emit('leave_lobby', {'lobby_id': lobbyId});
    await _socket.getWaitingLobbySocket.clearSockets();
    await _liveKit.disConnectRoom(_room);
    await _room.dispose();
    Get.back();
  }

  // send message
  void _sendMessage() async {
    if (textEditingController.text.isEmpty) {
      return;
    }
    // is only white space exist in
    bool? whiteSpace;
    Map data = {'message': textEditingController.text};
    for (var element in textEditingController.text.characters) {
      if (element.codeUnitAt(0) == 10 || element.codeUnitAt(0) == 32) {
        whiteSpace = true;
      } else {
        whiteSpace = false;
        break;
      }
    }

    if (whiteSpace == false) {
      textEditingController.clear();
      _socket.getWaitingLobbySocket
          .emit(event: 'waiting_lobby_message', data: data);
    }
  }

  // play sound
  void _playBubbleSound() {
    if (chatSound.isTrue) {
      _audioPlayer.play(AssetSource('bubble.mp3'));
    }
  }

  // kick player
  void kickPlayer(String playerId) {
    Map data = {'lobby_id': lobbyId, 'player_to_kick': playerId};
    _socket.getWaitingLobbySocket
        .emit(event: 'kick_user_from_lobby', data: data);
  }

  // delete lobby
  void deleteLobby() {
    Map data = {'lobby_id': lobbyId};
    _socket.getWaitingLobbySocket.emit(event: 'delete_lobby', data: data);
  }

  // specific player speech status
  bool specificPlayerSpeechStatus(String userId) {
    var find = speechList.firstWhereOrNull((element) => element.user == userId);
    if (find == null) {
      return false;
    } else {
      return find.speech;
    }
  }

  @override
  void onInit() {
    super.onInit();
    // create livekit room
    _room = _liveKit.createRoom;
    _liveKit.connectToRoom(_room, Get.arguments['token']);

    _socket.getWaitingLobbySocket.addListener(this);
    myUserId = _sharedPrefManager.readString('user_id')!;
    lobbyId = Get.arguments['lobby_id'];
    var creatorId = Get.arguments['creator_id'];

    if (creatorId == myUserId) {
      isCreator(true);
    }
    // get chats
    Future.delayed(
      const Duration(milliseconds: 500),
      () {
        _socket.getWaitingLobbySocket
            .emit(event: 'lobby_detail', data: {'lobby_id': lobbyId});
      },
    );
  }

  @override
  void onWaitingLobbyMessage(data) {
    LobbyWaitingMessageModel chatData =
        LobbyWaitingMessageModel.fromJson(data, myUserId);
    chatList.insert(0, chatData);

    chatListKey.currentState?.insertItem(
      0,
      duration: const Duration(milliseconds: 250),
    );
    _playBubbleSound();
    update(['chat']);
  }

  @override
  void onLobbyDetail(data) {
    var cleanData = LobbyDetailModel.fromJson(data['data']);
    playersData.clear();
    playersData.addAll(cleanData.players);

    update(['players']);

    // creator
    creatorData = cleanData.creator;
  }

  @override
  void onLobbyUpdateUsers(data) {
    var users = data['lobby_users'];
    playersData.clear();
    if (List.from(users).isNotEmpty) {
      var update = users
          .map<LobbyDetailPlayerModel>(
              (json) => LobbyDetailPlayerModel.fromJson(json))
          .toList();
      playersData.addAll(update);
    }

    update(['players']);
  }

  @override
  void onKickedPlayer() async {
    await _room.localParticipant?.setMicrophoneEnabled(false);
    await _socket.getWaitingLobbySocket.clearSockets();
    await _liveKit.disConnectRoom(_room);
    await _room.dispose();
    BotToast.showText(
        text: 'سازنده لابی شمارو از لابی اخراج کرد',
        duration: const Duration(seconds: 2));
    Get.back();
  }

  @override
  void onLobbyRemoved() async {
    await _room.localParticipant?.setMicrophoneEnabled(false);
    await _room.dispose();
    await _socket.getWaitingLobbySocket.clearSockets();
    BotToast.showText(
        text: 'لابی توسط سازنده بازی حذف شد',
        duration: const Duration(seconds: 2));
    Get.back();
  }

  @override
  void onError(data) {
    BotToast.showText(text: data['msg']);
  }

  @override
  void onGameStarted() async {
    await _room.localParticipant?.setMicrophoneEnabled(false);
    await _socket.getWaitingLobbySocket.clearSockets();
    micToggleStatus(false);
    _liveKit.participantSpeaking(_room, false);
    await _liveKit.disConnectRoom(_room);
    await _room.dispose();
    Get.offNamed(
      _hashRouting.getRoute(
        'lobby_game',
      ),
      arguments: {
        'is_player': myUserId != creatorData?.user_id,
        'is_creator': myUserId == creatorData?.user_id,
        'is_observer': false,
        'user_id': myUserId,
        'creator_id': creatorData?.user_id,
        'lobby_id': lobbyId
      },
    );
  }

  @override
  void onLobbySpeechStatus(data) {
    var list = data
        .map<LobbyWaitingSpeechModel>(
            (json) => LobbyWaitingSpeechModel.fromJson(json))
        .toList();
    speechList.clear();
    speechList.addAll(list);
    for (var element in speechList) {
      update([element.user]);
    }
  }
}
