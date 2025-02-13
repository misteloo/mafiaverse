import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/utils/constants/hash_routing.dart';

class SplashScreenController extends GetxController {
  // injection
  final SharedPrefManager _shared = Get.find();
  final HashRouting _routing = Get.find();
  final AudioPlayer _audioPlayer = Get.find();

  Future<bool> readUserToken() async {
    return await _shared.containData('token');
  }

  String getRoute(String key) => _routing.getRoute(key);

  void playShotSound() {
    _audioPlayer.play(AssetSource('pistol_sound.mp3'));
  }

  void getDetail() async {
    var tokenExist = await readUserToken();

    if (tokenExist) {
      await Get.offAndToNamed(getRoute('main_btm_nav_holder'));
    } else {
      await Get.offAndToNamed(getRoute('login'));
    }
  }
}
