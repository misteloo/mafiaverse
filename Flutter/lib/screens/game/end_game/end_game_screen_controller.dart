import 'package:audioplayers/audioplayers.dart';
import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/utils/constants/hash_routing.dart';

import '../../../models/game/end_game_result_model.dart';

class EndGameScreenController extends GetxController {
  EndGameScreenController() {
    scrollController.addListener(() {
      if (scrollController.position.userScrollDirection ==
          ScrollDirection.reverse) {
        visibleFab = false;
        update(['fab']);
      } else {
        visibleFab = true;
        update(['fab']);
      }
    });
  }
  // injection
  final HashRouting _routing = Get.find();
  final ApiRepository _apiRepository = Get.find();
  final SharedPrefManager _shared = Get.find();
  final AudioPlayer _audioPlayer = Get.find();

  late List<EndGameResultModel> users;
  ScrollController scrollController = ScrollController();
  bool visibleFab = true;

  void _bindUsers() {
    users = Get.arguments['users'];
    update(['users']);
  }

  void returnToHome() {
    if (!visibleFab) {
      return;
    }

    Get.until((route) =>
        Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
  }

  void reportPlayer({required String value, required String reportId}) async {
    Map body = {
      'token': _shared.readString('token'),
      'report_id': reportId,
      'kind': value,
      'game_id': Get.arguments['gameId']
    };

    var response = await _apiRepository.reportPlayer(body);

    response.fold((left) {
      BotToast.showText(text: 'خطا در ارسال');
    }, (right) {
      if (right.data['status']) {
        _audioPlayer.play(AssetSource('report_sound.mp3'));
      } else {
        BotToast.showText(text: '${right.data['msg']}');
      }
    });
  }

  @override
  void onInit() {
    super.onInit();
    _bindUsers();
  }
}
