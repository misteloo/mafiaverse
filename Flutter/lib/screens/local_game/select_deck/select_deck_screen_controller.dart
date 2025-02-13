import 'package:bot_toast/bot_toast.dart';
import 'package:get/get.dart';
import 'package:mafia/models/local_game/local_game_characters_model.dart';

class SelectDeckScreenController extends GetxController {
  // instances
  var deckList = List<int>.empty(growable: true);

  List<LocalGameCharactersModel> getCharacters = Get.arguments['deck'];
  int playerCount = Get.arguments['count'];

  bool isInDeckList(int id) {
    return deckList.contains(id);
  }

  void toggleItemAddRemove(int id) {
    if (deckList.contains(id)) {
      deckList.removeWhere((element) => element == id);
      update([id, 'total']);
    } else {
      deckList.add(id);
      update([id, 'total']);
    }
  }

  int itemCount(int id) => deckList.where((element) => element == id).length;

  void addItem(int id) {
    deckList.add(id);
    update([id, 'total']);
  }

  void removeItem(int id) {
    deckList.remove(id);
    update([id, 'total']);
  }

  void startGame() {
    if (playerCount == deckList.length) {
      Get.back(result: {'deck_list': deckList});
    } else {
      BotToast.showText(text: 'تعداد بازیکن با نقش برابر نیست');
    }
  }
}
