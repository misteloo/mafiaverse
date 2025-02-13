import 'dart:async';
import 'dart:convert';
import 'package:audioplayers/audioplayers.dart';
import 'package:circular_countdown_timer/circular_countdown_timer.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/game/select_character/select_character_character_model.dart';
import 'package:mafia/models/game/select_character/select_character_user_model.dart';
import 'package:mafia/screens/game/select_character/select_character_screen.dart';
import 'package:mafia/utils/constants/encrypt.dart';
import 'package:mafia/utils/constants/game_character.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/listeners/nato_game/socket_select_character_listener.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:mafia/utils/widget/custom_snack.dart';
import 'package:vibration/vibration.dart';

class SelectCharacterScreenController extends GetxController
    implements SocketSelectCharacterListener {
  SelectCharacterScreenController() {
    _socket.getSelectCharacterSocket.addListener(this);
    getUserId();
  }
  // injection
  final SharedPrefManager _shared = Get.find();
  final SocketManager _socket = Get.find();
  final HashRouting _router = Get.find();
  final GameCharacter _gameCharacter = Get.find();
  final AudioPlayer _audioPlayer = Get.find();
  // instance
  late String userId;
  List<SelectCharacterCharacterModel> characterList =
      List<SelectCharacterCharacterModel>.empty(growable: true);
  List<SelectCharacterUserModel> userList =
      List<SelectCharacterUserModel>.empty(growable: true);
  final GlobalKey<AnimatedListState> animatedListKey = GlobalKey();
  CountDownController countDownController = CountDownController();
  int timeLeft = 5;
  var myTurnToPick = false;

  /// vibrate
  void _startVibrate() async {
    if (await Vibration.hasVibrator() == true) {
      Vibration.vibrate();
    }
  }

  void getUserId() async {
    userId = _shared.readString('user_id')!;
  }

  void _readyToChooseCharacter() {
    Map<String, dynamic> json = {'op': 'ready_to_choose'};
    _socket.getSelectCharacterSocket.emit('game_handle', json);
  }

  void chooseCard(int index, String name) async {
    var indexObj = {'index': index};
    var obj = {'op': 'selected_character', 'data': indexObj};
    _socket.getSelectCharacterSocket.emit('game_handle', obj);

    var character = _gameCharacter.getCharacter('nato', name);
    // play sound
    _audioPlayer.play(AssetSource('bell.mp3'));
    // reveal character and navigation to game
    await Get.bottomSheet(
        SelectedCardReveal(
          image: character['image'].toString(),
          name: character['name'].toString(),
          dismissCallback: () async {
            // close bs
            Get.back();

            Get.offAndToNamed(
              _router.getRoute('nato_game'),
              arguments: {
                'isCreator': Get.arguments['isCreator'],
                'gameId': Get.arguments['gameId'],
                'userId': userId,
                'characterName': character['name'].toString(),
                'characterImage': character['image'].toString(),
                'serverCharacter': name,
                'fromReconnect': false
              },
            );
          },
        ),
        isDismissible: false,
        enableDrag: false);
  }

  @override
  void onAbandon() async {
    _startVibrate();
    await _socket.getSelectCharacterSocket.clearListeners();
    showCustomSnack(
        context: Get.context!,
        title: 'لغو بازی',
        body: 'بازی شما توسط سرور بخاطر عدم اتصال یکی از بازیکنان حذف  شد');
    Get.until(
      (route) => Get.currentRoute == _router.getRoute('main_btm_nav_holder'),
    );
  }

  @override
  void onCharacters(data) {
    var enc = encryptDecrypt(data['data']);
    var decode = jsonDecode(enc);

    var characters = decode
        .map<SelectCharacterCharacterModel>(
            (json) => SelectCharacterCharacterModel.fromJson(json))
        .toList() as List<SelectCharacterCharacterModel>;
    characterList.clear();
    characterList.addAll(characters);
    update(['characterList']);
  }

  @override
  void onRandomCharacter(data) async {
    var scenario = data['scenario'];
    var name = data['data']['name'];
    var character = _gameCharacter.getCharacter(scenario, name);
    // play sound
    _audioPlayer.play(AssetSource('bell.mp3'));
    // reveal character and navigation to game
    await Get.bottomSheet(
        SelectedCardReveal(
          image: character['image'].toString(),
          name: character['name'].toString(),
          dismissCallback: () async {
            // close bs
            Get.back();

            Get.offAndToNamed(
              _router.getRoute('nato_game'),
              arguments: {
                'isCreator': Get.arguments['isCreator'],
                'gameId': Get.arguments['gameId'],
                'userId': userId,
                'characterName': character['name'].toString(),
                'characterImage': character['image'].toString(),
                'serverCharacter': name,
                'fromReconnect': false
              },
            );
          },
        ),
        isDismissible: false,
        enableDrag: false);
  }

  @override
  void onUsersTurnToPick(data) {
    var users = data['data']
        .map<SelectCharacterUserModel>(
            (json) => SelectCharacterUserModel.fromJson(json))
        .toList() as List<SelectCharacterUserModel>;
    if (userList.isEmpty) {
      userList.addAll(users);
      animatedListKey.currentState?.insertAllItems(0, users.length);
      update(['userList']);
      _runTimer();
    } else {
      userList.removeAt(0);
      animatedListKey.currentState?.removeItem(
        0,
        (context, animation) =>
            UsersTurn(user: userList[0], isTurn: true, animation: animation),
      );
      update(['userList']);
      _runTimer();
    }

    if (users.first.user_id == userId) {}
  }

  Timer? _timer;
  void _runTimer() {
    timeLeft = 5;
    if (_timer != null) {
      _timer?.cancel();
    }
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (timeLeft == 0) {
        _timer?.cancel();
      }
      timeLeft--;
    });

    try {
      countDownController.pause();
      countDownController.restart();
    } catch (_) {}

    update(['counter']);
  }

  @override
  void onYourTurnToPick() {
    myTurnToPick = true;
    showCustomSnack(
        context: Get.context!,
        title: 'نوبت شماست',
        body: 'ازبین نقش ها انتخاب کن');
    _startVibrate();
  }

  @override
  void onInit() {
    super.onInit();
    _readyToChooseCharacter();
  }
}
