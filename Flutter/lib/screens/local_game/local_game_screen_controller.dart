import 'dart:convert';

import 'package:bot_toast/bot_toast.dart';
import 'package:get/get.dart';
import 'package:mafia/models/local_game/local_game_characters_model.dart';
import 'package:mafia/models/local_game/local_game_my_character_model.dart';
import 'package:mafia/models/local_game/local_game_users_model.dart';
import 'package:mafia/screens/local_game/local_game_screen.dart';
// import 'package:mafia/screens/qr_scanner/qr_scanner_screen.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/listeners/local/socket_local_game_listeners.dart';
import 'package:mafia/utils/socket/socket_manager.dart';

class LocalGameScreenController extends GetxController
    implements SocketLocalGameListener {
  // injection
  final SocketManager _socket = Get.find();
  final HashRouting _routing = Get.find();
  // instances
  final List<LocalGameCharactersModel> deckList =
      List<LocalGameCharactersModel>.empty(growable: true);
  final List<LocalGameUserModel> localGameUsers =
      List<LocalGameUserModel>.empty(growable: true);

  bool canStartTheGame = false;
  bool gamePanel = false;
  bool gameStarted = false;

  final LocalGameMyCharacterModel myCharacter = LocalGameMyCharacterModel();

  int _playerCount = 0;

  void _initListeners() {
    _socket.getLocalGameSocket.addListener(this);
  }

  void navigateToQrScanner(String playerName) async {
    // Get.to(() => QrCodeScannerScreen(
    //       gameId: (id) {
    //         Get.back();
    //         // join
    //         Future.delayed(const Duration(milliseconds: 500), () {
    //           _joinLocalGame(playerName, id);
    //         });
    //       },
    //     ));
  }

  void _joinLocalGame(String playerName, String gameId) {
    Map data = {'game_id': gameId, 'name': playerName};

    Map op = {'op': 'join_local_game', 'data': data};

    _socket.getLocalGameSocket.emit('handle_local_game', op);
  }

  void startLocalGame() {
    // close Qr
    if (Get.isDialogOpen ?? false) {
      Get.back();
    }
    Map op = {'op': 'pick_cart'};
    // emit service start game
    _socket.getLocalGameSocket.emit('handle_local_game', op);
    // change panel to game
    gamePanel = true;
    update(['panel']);
  }

  void removeLocalGame() {
    gamePanel = false;
    gameStarted = false;
    myCharacter
      ..icon = null
      ..name = null
      ..side = null;

    update(['panel', 'gameStarted']);
  }

  void createLocalGame(int playerCount) {
    _playerCount = playerCount;
    Map op = {'player_count': playerCount};
    _socket.getLocalGameSocket.emit('create_local_game', op);

    // get deck
    var op2 = {'op': 'get_deck'};
    _socket.getFindMatchSocket.emit('handle_local_game', op2);
  }

  @override
  void onCard(data) {
    var parse = data['data'];
    String side = parse['side'];
    String icon = parse['icon'];
    String name = parse['name'];
    myCharacter
      ..icon = icon
      ..name = name
      ..side = side;
    // close open dialog
    if (Get.isDialogOpen ?? false) {
      Get.back();
    }

    // change panel
    gamePanel = true;
    gameStarted = true;
    update(['panel', 'gameStarted']);
  }

  @override
  void onError(data) {
    var parse = data['data'];
    BotToast.showText(text: parse['msg']);
  }

  @override
  void onGetDeck(data) {
    var parse = data['data'];
    var deck = parse
        .map<LocalGameCharactersModel>(
            (json) => LocalGameCharactersModel.fromJson(json))
        .toList() as List<LocalGameCharactersModel>;

    deckList.clear();
    deckList.addAll(deck);

    Get.toNamed(_routing.getRoute('select_deck'),
        arguments: {'deck': deckList, 'count': _playerCount})?.then((value) {
      try {
        // selected deck
        var selectedDeck = value['deck_list'] as List<int>;
        // json array

        List<Map<String, dynamic>> jsonList =
            selectedDeck.map((e) => {'id': e}).toList();

        var op = {'op': 'set_deck', 'data': jsonList};

        // emit
        _socket.getLocalGameSocket.emit('handle_local_game', op);
      } catch (_) {}
    });
  }

  @override
  void onLocalGameStarted(data) {
    var qrString = data['data']['qr_code'] as String;
    var startIndex = qrString.indexOf(',');
    var qr = qrString.substring(startIndex + 1, qrString.length);
    // show qr code
    var qrImage = const Base64Decoder().convert(qr);
    // bs
    Get.bottomSheet(
      ShowQrCodeWidget(
        qrCodeImage: qrImage,
        controller: this,
      ),
      isDismissible: false,
      enableDrag: false,
    );
  }

  @override
  void onPrvDeck(data) {
    Get.dialog(LocalGameIdleToStart(exit: () {
      Get.back();
      Map op = {'op': 'leave_local_game'};
      _socket.getLocalGameSocket.emit('handle_local_game', op);
    }), barrierDismissible: false);
  }

  @override
  void onUsers(data) {
    var parse = data['data']['users'];
    parse
        .map<dynamic>((json) => localGameUsers
            .singleWhere((element) => element.user_id == json['user_id'])
          ..playerCard = json['cart'])
        .toList();
    gameStarted = true;

    update(['gameStarted']);
  }

  @override
  void onUsersJoin(data) {
    var parse = data['data']['users'];
    var canStart = data['data']['can_start'] as bool;

    canStartTheGame = canStart;
    update(['canStart']);
    // users
    var users = parse
        .map<LocalGameUserModel>((json) => LocalGameUserModel.fromJson(json))
        .toList();

    localGameUsers.clear();
    localGameUsers.addAll(users);

    update(['localUsers']);
  }

  @override
  void onInit() {
    super.onInit();
    _initListeners();
  }
}
