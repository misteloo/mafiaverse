import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';

class ReportBugScreenController extends GetxController {
  // injection
  final ApiRepository _api = Get.find();
  final SharedPrefManager _shared = Get.find();

  // instances
  var loading = false;
  TextEditingController titleController = TextEditingController();
  TextEditingController contentController = TextEditingController();

  void sendReport() async {
    loading = true;
    update(['loading']);
    Map body = {
      'token': _shared.readString('token'),
      'msg':
          'عنوان : ${titleController.text} \n متن : ${contentController.text}'
    };

    var response = await _api.reportBug(body);

    response.fold((left) {
      loading = false;
      update(['loading']);

      BotToast.showText(text: 'خطا در برقراری ارتباط');
    }, (right) {
      loading = false;
      update(['loading']);
      if (right.data['status'] as bool == true) {
        BotToast.showText(text: 'گزارش شما با موفقیت ثبت شد');
        contentController.clear();
        titleController.clear();
      }
    });
  }
}
