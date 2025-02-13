import 'package:get/get.dart';
import 'package:mafia/utils/constants/hash_routing.dart';

class MainController extends GetxController {
  // injection
  final HashRouting _routing = Get.find();
  String? appRoute;
  // instance
  bool _backSoundPlay = true;

  String initialRout() => _routing.getRoute('splash');
  String getPage(String key) => _routing.getRoute(key);

  Future<bool> backSoundToggle() async {
    return _backSoundPlay;
  }

  void modifyBackgroundMusic(Routing? currentRoute) async {
    appRoute = currentRoute?.current;
    // if (!_backSoundPlay) {
    //   return;
    // } else if (currentRoute?.current == '/main_btm_nav_holder') {
    //   try {
    //     _audio.play(AssetSource('background_music.mp3'));
    //   } catch (_) {}
    // } else if (currentRoute?.current == '/find_match') {
    //   await stopAudio();
    //   try {
    //     _audio.play(AssetSource('find_match.mp3'));
    //   } catch (_) {}
    // } else if (currentRoute?.current == '/select_character') {
    //   await stopAudio();
    // } else if (currentRoute?.current == '/reconnect') {
    //   await stopAudio();
    // } else if (currentRoute?.current == '/nato_game') {
    //   await stopAudio();
    // }
  }
}
