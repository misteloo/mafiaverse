import 'package:get/get.dart';
import 'package:mafia/screens/profile/edit_profile/edit_profile_screen_controller.dart';

class EditProfileScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => EditProfileScreenController());
  }
}
