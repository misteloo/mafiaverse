import 'package:bot_toast/bot_toast.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:permission_handler/permission_handler.dart';

import '../../../utils/widget/custom_snack.dart';

class SignUpScreenController extends GetxController {
  final ApiRepository _api = Get.find();
  final HashRouting _router = Get.find();

  TextEditingController usernameField = TextEditingController();
  TextEditingController phoneField = TextEditingController();
  TextEditingController introducerField = TextEditingController();
  var rxIntroducer = false;
  var loading = false;
  var rxPrivacyPolicy = false.obs;
  String? errorUsername, errorPhone;
  String routing(String key) => _router.getRoute(key);

  void checkInputs(String phone, String username) async {
    errorPhone = null;
    errorUsername = null;
    if (username.isEmpty) {
      errorUsername = 'نام کاربری رو بنویس';
      update();
      return;
    }

    if (phone.length < 9) {
      errorPhone = 'شماره تماس به درستی وارد نشده';
      update();
      return;
    } else {
      errorPhone = null;
      update();

      // check permission notification
      if (await Permission.notification.isGranted) {
        signUpRequest(username, phone);
      } else {
        showCustomSnack(
          context: Get.context!,
          title: 'خطا',
          body: 'دسترسی اعلان داده نشده',
        );
      }
    }
  }

  void signUpRequest(String username, String phone) async {
    loading = true;
    update(['loading']);
    var body = {
      'phone': '09$phone',
      'name': username,
      'firebase_token': await FirebaseMessaging.instance.getToken(),
    };

    var response = await _api.singUpRequest(body);

    response.fold((left) {
      BotToast.showText(text: 'خطا در ارتباط');
      loading = false;
      update(['loading']);
    }, (right) {
      loading = false;
      update(['loading']);
      if (right.data['status']) {
        Get.toNamed(routing('confirm_code'), arguments: {
          'from': 'sign_up',
          'phone': '09${phoneField.text}',
          'introducer': introducerField.text
        });
      } else {
        BotToast.showText(text: right.data['msg']);
      }
    });
  }
}
