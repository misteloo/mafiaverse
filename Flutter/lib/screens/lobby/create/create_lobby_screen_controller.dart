import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/lobby/last_card_model.dart';
import 'package:mafia/models/lobby/lobby_character_model.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/extension/eng_num_to_arabic.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_create_listeners.dart';
import 'package:mafia/utils/socket/socket_manager.dart';

class CreateLobbyScreenController extends GetxController
    implements SocketCreateLobbyListeners {
  // injection
  final ApiRepository _api = Get.find();
  final SocketManager _socketManager = Get.find();
  final HashRouting _routing = Get.find();
  final SharedPrefManager _sharedPrefManager = Get.find();

  // instances
  TextEditingController lobbyNameController = TextEditingController();
  TextEditingController lobbyScenarioController = TextEditingController();
  TextEditingController lobbyPassWordController = TextEditingController();
  TextEditingController createGameSideEditingController =
      TextEditingController();
  final TextEditingController lastCardController = TextEditingController();

  var gameSideKey = GlobalKey<AnimatedListState>();
  var lastCardKey = GlobalKey<AnimatedListState>();
  var createdDeckKey = GlobalKey<AnimatedListState>();

  var lastMoveCardList = List<LastMoveCardModel>.empty(growable: true).obs;
  var characterList = List<LobbyCharacterModel>.empty(growable: true).obs;
  var sideNameList = List<String>.empty(growable: true).obs;

  bool isPrivateLobby = false;
  var lastCardCount = 0.obs;
  var playerCount = 0.obs;
  var deckLoading = false;
  var loadingCreation = false;
  late String? myUserId;

  // func

  bool privateLobby() => isPrivateLobby;

  void createDeck(List<LobbyCharacterModel> decks) {
    var items = decks.where((element) => element.count > 0).toList();
    characterList.clear();
    characterList.addAll(items);
    characterList.refresh();
  }

  Future<void> getCharacters() async {
    deckLoading = true;
    update(['deckLoading']);

    var response = await _api.getCharacterListForLobby();

    response.fold((left) {
      deckLoading = false;
      update(['deckLoading']);
      BotToast.showText(text: 'خطا در دریافت نقش');
    }, (right) {
      deckLoading = false;
      update(['deckLoading']);
      var characterList = right.data['data']['deck']
          .map<LobbyCharacterModel>(
              (json) => LobbyCharacterModel.fromJson(json))
          .toList() as List<LobbyCharacterModel>;

      this.characterList.clear();
      this.characterList.addAll(characterList);
      update(['deckList']);
    });

    return Future.value(null);
  }

  void incPlayer() {
    playerCount.value++;
    playerCount.refresh();
  }

  void decPlayer() {
    if (playerCount.value > 0) {
      playerCount.value--;
      playerCount.refresh();
    }
  }

  void createLastMoveCard(String name) {
    lastMoveCardList.add(LastMoveCardModel(name: name));
    // clear edt
    lastCardController.clear();
    // update list
    lastCardKey.currentState?.insertItem(
      0,
      duration: const Duration(milliseconds: 250),
    );
    lastMoveCardList.refresh();
  }

  void createGame() {
    loadingCreation = true;
    update(['loading']);
    if (lobbyNameController.text.isEmpty ||
        lobbyScenarioController.text.isEmpty) {
      BotToast.showText(text: 'اطلاعات لازم وارد نشده');
      return;
    } else if (privateLobby() && lobbyPassWordController.text.isEmpty) {
      BotToast.showText(text: 'رمز تعریف نشده');
      return;
    } else if (characterList.where((p0) => p0.count > 0).toList().isEmpty) {
      BotToast.showText(text: 'نقش های بازی مشخص نشده');
      return;
    } else if (sideNameList.isEmpty) {
      BotToast.showText(text: 'ساید های بازی تعریف نشده');
      return;
    } else {
      Map data = {
        'name': lobbyNameController.text,
        'scenario': lobbyScenarioController.text,
        'private': isPrivateLobby,
        'password': lobbyPassWordController.text.isEmpty
            ? null
            : lobbyPassWordController.text.toArabic(),
        'player_cnt': playerCount.value,
        'cards': lastMoveCardList.isNotEmpty
            ? lastMoveCardList.map((element) => {'name': element.name}).toList()
            : [],
        'characters': characterList
            .where((p0) => p0.count > 0)
            .map((element) => {
                  'name': element.name,
                  'custom': element.custom,
                  'count': element.count,
                  'id': element.id,
                  'custom_side': element.customSide
                })
            .toList(),
        'sides': sideNameList.map((element) => element).toList()
      };
      _socketManager.emit('create_lobby', data);
    }
  }

  @override
  void onInit() {
    super.onInit();
    sideNameList.add('مافیا');
    sideNameList.add('شهروند');
    _socketManager.getCreateLobbySocket.addListener(this);
    isPrivateLobby = Get.arguments['private'];
    myUserId = _sharedPrefManager.readString('user_id');
  }

  @override
  void createLobbyResult(data) async {
    loadingCreation = false;
    update(['loading']);
    await _socketManager.getCreateLobbySocket.offSockets();
    await Get.offNamed(
      _routing.getRoute(
        'lobby_waiting_room',
      ),
      arguments: {
        'lobby_id': data['lobby_id'],
        'creator_id': myUserId,
        'token': data['token']
      },
    );
  }
}
