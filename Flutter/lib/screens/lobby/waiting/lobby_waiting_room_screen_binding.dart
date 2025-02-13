import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/lobby/waiting/lobby_waiting_room_screen_controller.dart';
import 'package:mafia/utils/voice/live_kit_manager.dart';

class LobbyWaitingRoomScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => LobbyWaitingRoomScreenController());
    Get.lazyPut(() => AudioPlayer());
    Get.lazyPut(() => LiveKitManager());
  }
}
