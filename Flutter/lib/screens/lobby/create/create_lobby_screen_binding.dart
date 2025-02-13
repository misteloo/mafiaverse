import 'package:get/get.dart';
import 'package:mafia/screens/lobby/create/create_lobby_screen_controller.dart';

class CreateLobbyScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => CreateLobbyScreenController());
  }
}
