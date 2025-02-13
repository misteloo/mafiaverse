import 'package:get/get.dart';
import 'package:mafia/screens/leader_board/leader_board_screen_controller.dart';

class LeaderBoardScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => LeaderBoardScreenController());
  }
}
