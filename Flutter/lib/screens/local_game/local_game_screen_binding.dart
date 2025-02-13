import 'package:get/get.dart';
import 'package:mafia/screens/local_game/local_game_screen_controller.dart';

class LocalGameScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => LocalGameScreenController());
  }
}
