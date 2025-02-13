import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/game/nato_game/nato_screen_controller.dart';
import 'package:mafia/utils/voice/live_kit_manager.dart';

class NatoScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => NatoScreenController());
    Get.lazyPut(() => LiveKitManager());
    Get.lazyPut(() => AudioPlayer());
  }
}
