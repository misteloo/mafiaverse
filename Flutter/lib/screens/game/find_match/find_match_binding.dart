import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/game/find_match/find_match_screen_controller.dart';

class FindMatchScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => FindMatchScreenController());
    Get.lazyPut(() => AudioPlayer());
  }
}
