import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/lobby/game/lobby_game_screen_controller.dart';

import '../../../utils/voice/live_kit_manager.dart';

class LobbyGameScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => LobbyGameScreenController());
    Get.lazyPut(() => LiveKitManager());
    Get.lazyPut(() => AudioPlayer());
  }
}
