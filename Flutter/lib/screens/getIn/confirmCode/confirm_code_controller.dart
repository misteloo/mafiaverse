import 'dart:async';
import 'package:bot_toast/bot_toast.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/utils/constants/hash_routing.dart';

class ConfirmCodeScreenController extends GetxController {
  final ApiRepository _api = Get.find();
  final SharedPrefManager _shared = Get.find();
  final HashRouting _router = Get.find();
  TextEditingController confirmCodeEditing = TextEditingController();
  String? errorEdtConfirm;
  var rxTimer = 30.obs;
  Timer? _timer;
  var loading = false;

  String routing(String key) => _router.getRoute(key);

  void startCountDownTimer() {
    if (_timer != null) _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (rxTimer > 0) {
        rxTimer.value--;
      } else {
        rxTimer(0);
        _timer?.cancel();
      }
    });
  }

  void checkInput(String txt) {
    errorEdtConfirm = null;
    if (txt.isEmpty) {
      errorEdtConfirm = 'کد تایید وارد نشده';
      update(['edtConfirmCode']);
    } else {
      update(['edtConfirmCode']);
      _requestConfirmCode(txt);
    }
  }

  void _requestConfirmCode(String txt) async {
    loading = true;
    update(['loading']);
    Either response;
    // from sign up

    if (Get.arguments['from'] == 'sign_up') {
      var body = {
        'code': txt,
        'phone': Get.arguments['phone'],
        'introducer': Get.arguments['introducer']
      };
      response = await _api.signUpConfirmPhone(body);
    }
    // from login
    else {
      var body = {
        'code': txt,
        'phone': Get.arguments['phone'],
      };
      response = await _api.loginConfirmPhone(body);
    }

    response.fold(
      (left) {
        BotToast.showText(text: 'عدم ارتباط');
        loading = false;
        update(['loading']);
      },
      (right) {
        loading = true;
        update(['loading']);
        if (right.data['status']) {
          // store token and navigation
          _storeToken(right.data['data']['token']);
          loading = false;
          update(['loading']);
        } else {
          BotToast.showText(text: right.data['msg']);
          loading = false;
          update(['loading']);
        }
      },
    );
  }

  void _storeToken(String token) async {
    await _shared.writeData('token', token);
    await Get.offAllNamed(routing('main_btm_nav_holder'));
  }

  @override
  InternalFinalCallback<void> get onDelete {
    if (_timer != null) _timer?.cancel();
    return super.onDelete;
  }
}
