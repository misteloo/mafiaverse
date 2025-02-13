import 'package:get/get.dart';
import 'package:mafia/screens/getIn/confirmCode/confirm_code_controller.dart';

class ConfirmCodeScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => ConfirmCodeScreenController(), fenix: true);
  }
}
