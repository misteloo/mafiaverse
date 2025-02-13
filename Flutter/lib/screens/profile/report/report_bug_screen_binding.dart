import 'package:get/get.dart';
import 'package:mafia/screens/profile/report/report_bug_screen_controller.dart';

class ReportBugScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => ReportBugScreenController());
  }
}
