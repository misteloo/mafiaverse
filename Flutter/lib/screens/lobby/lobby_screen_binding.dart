import 'package:get/get.dart';
import 'package:mafia/screens/lobby/lobby_screen_controller.dart';

class LobbyScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => LobbyScreenController());
  }
}
