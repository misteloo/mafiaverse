import 'package:bot_toast/bot_toast.dart';
import 'package:either_dart/either.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/screens/getIn/confirmCode/confirm_code_binding.dart';
import 'package:mafia/screens/getIn/confirmCode/confirm_code_screen.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:mafia/utils/widget/custom_snack.dart';
import 'package:permission_handler/permission_handler.dart';

class LoginScreenController extends GetxController {
  // injections
  final ApiRepository _api = Get.find();
  final HashRouting _router = Get.find();
  final SocketManager _socket = Get.find();

  TextEditingController textController = TextEditingController();
  var rxErrorField = RxnString();
  var loading = false;
  String routing(String key) => _router.getRoute(key);

  void checkPhoneInput(String phone) async {
    if (phone.length < 9) {
      rxErrorField('شماره تماس به درستی وارد نشده');
    } else {
      rxErrorField.value = null;
      // check notification permission

      if (await Permission.notification.isGranted) {
        // request
        _loginRequest(phone);
      } else {
        showCustomSnack(
          context: Get.context!,
          title: 'خطا',
          body: 'دسترسی اعلان داده نشده',
        );
      }
    }
  }

  _loginRequest(String phone) async {
    loading = true;
    update(['loading']);
    String? fireBase = await FirebaseMessaging.instance.getToken();
    Map<String, dynamic> body = {
      'phone': '09$phone',
      'firebase_token': fireBase
    };
    Either response = await _api.loginRequest(body);

    response.fold((left) {
      BotToast.showText(text: 'عدم اتصال به اینترنت');
      loading = false;
      update(['loading']);
    }, (response) {
      if (response.data['status'] as bool == true) {
        showCustomSnack(
          context: Get.context!,
          title: 'تبریک',
          body: '${response.data['msg']}',
        );
        loading = false;
        update(['loading']);
        // navigate to confirm code
        Get.to(
          () => const ConfirmCodeScreen(),
          binding: ConfirmCodeScreenBinding(),
          arguments: {'phone': '09$phone', 'from': 'login'},
        );
      } else {
        showCustomSnack(
          context: Get.context!,
          title: 'خطا',
          body: '${response.data['msg']}',
        );
        loading = false;
        update(['loading']);
      }
    });
  }

  void checkArg() async {
    try {
      var exit = Get.arguments['exitAccount'];

      if (exit != null) {
        if (exit) {
          await Future.delayed(const Duration(milliseconds: 500), () {
            _socket.disconnect();
          });
          Future.delayed(const Duration(milliseconds: 500), () {
            _socket.connect();
          });
        }
      }
    } catch (_) {}
  }

  @override
  void onInit() {
    // arg
    checkArg();
    super.onInit();
  }
}
