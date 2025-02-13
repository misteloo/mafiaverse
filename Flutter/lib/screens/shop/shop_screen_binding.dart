import 'package:get/get.dart';
import 'package:mafia/screens/shop/shop_screen_controller.dart';

class ShopScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => ShopScreenController());
  }
}
