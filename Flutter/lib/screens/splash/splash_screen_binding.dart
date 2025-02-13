import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/screens/splash/splash_controller.dart';

class SplashScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => SplashScreenController(), fenix: true);
    Get.lazyPut(() => AudioPlayer());
  }
}
