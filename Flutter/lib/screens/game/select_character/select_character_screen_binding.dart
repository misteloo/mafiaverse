import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/game/select_character/select_character_screen_controller.dart';
import 'package:mafia/utils/constants/game_character.dart';

class SelectCharacterScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => SelectCharacterScreenController());
    Get.lazyPut(() => GameCharacter());
    Get.lazyPut(() => AudioPlayer());
  }
}
