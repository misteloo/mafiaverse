// ignore_for_file: invalid_use_of_visible_for_testing_member, invalid_use_of_protected_member

import 'package:bot_toast/bot_toast.dart';
import 'package:easy_refresh/easy_refresh.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/lobby/lobby_list_model.dart';
import 'package:mafia/models/profile/check_other_profile_model.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/extension/eng_num_to_arabic.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_listener.dart';
import 'package:mafia/utils/socket/socket_manager.dart';

class LobbyScreenController extends GetxController
    implements SocketLobbyListener {
  // injection
  final ApiRepository _api = Get.find();
  final SocketManager _socket = Get.find();
  final HashRouting _routing = Get.find();
  final SharedPrefManager _sharedPrefManager = Get.find();

  // instances
  var showFab = true.obs;
  var lobbyListLoading = false.obs;
  var unStartedGamesList = List<LobbyListModel>.empty(growable: true);
  var startedGamesList = List<LobbyListModel>.empty(growable: true);
  var selfGamesList = List<LobbyListModel>.empty(growable: true);

  TextEditingController passwordTextEditing = TextEditingController();
  EasyRefreshController scrollController = EasyRefreshController();
  bool loadingProfile = false;

  String? userId;

  // list
  void getLobbyList({required String type}) async {
    // self // started // un_started
    Map body = {
      'type': type,
      'token': _sharedPrefManager.readString('token'),
    };

    var response = await _api.getLobbyGames(body);

    response.fold((left) {
      BotToast.showText(text: 'خطا در دریافت بازی ها');
      try {
        scrollController.finishRefresh(IndicatorResult.fail, true);
      } catch (_) {}
    }, (right) {
      scrollController.finishRefresh(IndicatorResult.success, true);
      List<LobbyListModel> data = right.data['data']['lobby_list']
          .map<LobbyListModel>((json) => LobbyListModel.fromJson(json))
          .toList() as List<LobbyListModel>;
      switch (type) {
        case 'un_started':
          unStartedGamesList.clear();
          unStartedGamesList.addAll(data);
          update(['unStartedList']);
          break;
        case 'started':
          startedGamesList.clear();
          startedGamesList.addAll(data);
          update(['startedList']);
          break;
        case 'self':
          selfGamesList.clear();
          selfGamesList.addAll(data);
          update(['selfList']);
          break;
      }
    });
  }

  // join online game
  void reconnectToGame(String lobbyId) {
    var res = selfGamesList
        .firstWhereOrNull((element) => element.lobby_id == lobbyId);
    if (res != null) {
      if (res.started == false) {
        BotToast.showText(text: 'بازی هنوز شروع نشده');
        return;
      }
      // is creator
      if (res.creator.user_id == userId) {
        Get.toNamed(_routing.getRoute('lobby_game'), arguments: {
          'is_player': false,
          'is_creator': true,
          'is_observer': false,
          'creator_id': res.creator.user_id,
          'user_id': userId,
          'lobby_id': lobbyId
        });
      } else {
        if (res.players
                .firstWhereOrNull((element) => element.user_id == userId) !=
            null) {
          Get.toNamed(_routing.getRoute('lobby_game'), arguments: {
            'is_player': true,
            'is_creator': res.creator.user_id == userId,
            'is_observer': false,
            'creator_id': res.creator.user_id,
            'user_id': userId,
            'lobby_id': lobbyId
          });
        } else {
          BotToast.showText(text: 'شما عضو بازیکنان نیستی');
        }
      }
    }
  }

  // observer
  void joinToGameAsObserver(String lobbyId) {
    var res = startedGamesList
        .firstWhereOrNull((element) => element.lobby_id == lobbyId);
    if (res != null) {
      if (res.started == false) {
        BotToast.showText(text: 'بازی هنوز شروع نشده');
        return;
      }

      if (res.players
                  .firstWhereOrNull((element) => element.user_id == userId) ==
              null &&
          res.creator.user_id != userId) {
        Get.toNamed(_routing.getRoute('lobby_game'), arguments: {
          'is_player': false,
          'is_creator': res.creator.user_id == userId,
          'is_observer': true,
          'creator_id': res.creator.user_id,
          'user_id': userId,
          'lobby_id': lobbyId
        });
      } else {
        BotToast.showText(text: 'شما نمیتونی به عنوان بیننده واردشی');
      }
    }
  }

  // check profile
  void checkProfile(
      {required LobbyListModel lobby,
      required Function(CheckOtherProfileModel) profileCallback}) async {
    if (!loadingProfile) {
      loadingProfile = true;

      // update user
      lobby.creator.loadingProfile = true;
      update([lobby.lobby_id]);

      Map body = {'user_id': lobby.creator.user_id};
      var response = await _api.checkProfile(body);

      response.fold((left) {
        // update user
        lobby.creator.loadingProfile = false;
        update([lobby.lobby_id]);
        loadingProfile = false;
        BotToast.showText(text: 'خطا در دریافت اطلاعات');
      }, (right) {
        // update user
        lobby.creator.loadingProfile = false;
        update([lobby.lobby_id]);
        loadingProfile = false;

        var parse = CheckOtherProfileModel.fromJson(right.data['data']['data']);
        profileCallback(parse);
      });
    }
  }

  void navigateToCreateLobby(bool private) {
    Get.toNamed(_routing.getRoute('create_lobby'),
        arguments: {'private': private});
  }

  void joinToGame(LobbyListModel lobby, String? password) {
    passwordTextEditing.clear();
    Map data = {
      'lobby_id': lobby.lobby_id,
      'password': password.toString().toArabic()
    };
    _socket.emit('join_lobby', data);
  }

  @override
  void onInit() {
    super.onInit();
    userId = _sharedPrefManager.readString('user_id');
    _socket.getLobbySocket.addListener(this);
  }

  @override
  void onError(data) {
    var msg = data['msg'];
    BotToast.showText(text: msg);
    // return to lobby lists
    Get.until((route) =>
        Get.currentRoute == _routing.getRoute('main_btm_nav_holder'));
  }

  @override
  void onLobbyJoinResult(data) async {
    var status = data['status'] as bool;
    var token = data['token'] as String;
    var msg = data['msg'] as String;
    var _ = data['is_creator'] as bool?;
    var creatorId = data['creator_id'];
    var lobbyId = data['lobby_id'] as String?;

    if (status) {
      // navigate
      Get.toNamed(
        _routing.getRoute(
          'lobby_waiting_room',
        ),
        arguments: {
          'lobby_id': lobbyId,
          'creator_id': creatorId,
          'user_id': userId,
          'token': token
        },
      );
    } else {
      BotToast.showText(text: msg);
    }
  }
}
