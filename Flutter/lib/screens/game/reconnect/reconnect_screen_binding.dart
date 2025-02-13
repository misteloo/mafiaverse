import 'package:get/get.dart';
import 'package:mafia/screens/game/reconnect/reconnect_screen_controller.dart';

import '../../../database/shared/shared_pref_manager.dart';

class ReconnectScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => SharedPrefManager());
    Get.lazyPut(() => ReconnectScreenController());
  }
}
