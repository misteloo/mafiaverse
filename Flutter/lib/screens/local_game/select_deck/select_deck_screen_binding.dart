import 'package:get/get.dart';
import 'package:mafia/screens/local_game/select_deck/select_deck_screen_controller.dart';

class SelectDeckScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => SelectDeckScreenController());
  }
}
