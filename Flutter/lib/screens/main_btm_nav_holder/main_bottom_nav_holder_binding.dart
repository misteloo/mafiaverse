import 'package:get/get.dart';
import 'package:mafia/payment/myket_payment.dart';
import 'package:mafia/screens/lobby/lobby_screen_controller.dart';
import 'package:mafia/screens/home/home_screen_controller.dart';
import 'package:mafia/screens/leader_board/leader_board_screen_controller.dart';
import 'package:mafia/screens/main_btm_nav_holder/main_bottom_nav_holder_controller.dart';
import 'package:mafia/screens/profile/profile_screen_controller.dart';
import 'package:mafia/screens/shop/shop_screen_controller.dart';

import '../../payment/bazzar_payment.dart';

class MainBottomNavHolderBinding extends Bindings {
  @override
  void dependencies() {
    Get.put(MainBottomNavHolderController());
    Get.lazyPut(() => HomeScreenController(), fenix: true);
    Get.lazyPut(() => ShopScreenController(), fenix: true);
    Get.lazyPut(() => LeaderBoardScreenController(), fenix: true);
    Get.lazyPut(() => ProfileScreenController(), fenix: true);
    Get.lazyPut(() => LobbyScreenController(), fenix: true);
    Get.lazyPut(() => BazzarPayment(), fenix: true);
    Get.lazyPut(() => MyketPayment(), fenix: true);
  }
}
