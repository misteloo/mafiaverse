import 'package:get/get.dart';
import 'package:mafia/screens/game/end_game/end_game_screen_controller.dart';
import 'package:mafia/utils/constants/hash_routing.dart';

class EndGameScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => EndGameScreenController());
    Get.lazyPut(() => HashRouting());
  }
}
