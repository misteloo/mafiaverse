import 'package:get/get.dart';
import 'package:mafia/screens/getIn/signUp/signup_screen_controller.dart';

class SignUpScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => SignUpScreenController());
  }
}
