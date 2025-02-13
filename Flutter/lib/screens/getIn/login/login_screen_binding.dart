import 'package:get/get.dart';
import 'package:mafia/screens/getIn/login/login_screen_controller.dart';

class LoginScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => LoginScreenController(), fenix: true);
  }
}
