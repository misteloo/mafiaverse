import 'dart:async';

import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/profile/user_items_model.dart';

class EditProfileScreenController extends GetxController {
  // injections
  final ApiRepository _api = Get.find();
  final SharedPrefManager _shared = Get.find();

  // instances
  List<UserItemsModel> avatarList = List<UserItemsModel>.empty(growable: true);
  TextEditingController textEditingController = TextEditingController();
  Timer? _debounce;
  String? selectedAvatarImage;
  String? suffixText;
  bool suffixError = false;
  bool loadingCheckUsername = false;

  void changeAvatar(String id) {
    for (var element in avatarList) {
      element.active = false;
    }

    avatarList.singleWhere((element) => element.id == id).active = true;
    update(['editAvatar', 'avatars']);
  }

  // get avatar list
  void getAvatars() async {
    var token = _shared.readString('token');
    var body = {'token': token};

    var response = await _api.getUserAvatarList(body);

    response.fold((left) {
      BotToast.showText(text: 'خطا در دریافت اطلاعات');
    }, (right) {
      var items = right.data['data']['items']
          .map<UserItemsModel>((json) => UserItemsModel.fromJson(json))
          .toList();
      avatarList.clear();
      avatarList.addAll(items);
      update(['avatars', 'editAvatar']);
    });
  }

  onCheckUsername(String query) {
    if (_debounce?.isActive ?? false) _debounce?.cancel();
    _debounce = Timer(const Duration(milliseconds: 500), () {
      if (query.isEmpty) {
        suffixError = false;
        suffixText = null;
        update(['username']);
        return;
      }
      // check name from server
      _checkUserName(query);
    });
  }

  // check user name
  _checkUserName(String newName) async {
    loadingCheckUsername = true;
    update(['username', 'loadingUsername']);
    Map body = {'new_name': newName};
    var response = await _api.checkUserName(body);

    response.fold((left) {
      BotToast.showText(text: 'خطا در ارتباط');
      loadingCheckUsername = false;
      update(['username', 'loadingUsername']);
    }, (right) {
      loadingCheckUsername = false;
      if (right.data['status']) {
        suffixText = 'مجاز';
        suffixError = false;
        update(['username', 'loadingUsername']);
      } else {
        suffixText = 'استفاده شده';
        suffixError = true;
        update(['username', 'loadingUsername']);
      }
    });
  }

  Future<bool> changeUsername() async {
    Map body = {
      'token': _shared.readString('token'),
      'new_name': textEditingController.text.toString()
    };

    var response = await _api.changeUsername(body);

    return response.fold<bool>((left) {
      BotToast.showText(text: 'خطا در ارتباط');
      return false;
    }, (right) {
      if (right.data['status']) {
        return true;
      } else {
        BotToast.showText(text: right.data['msg']);
        return false;
      }
    });
  }

  Future<bool> changeAvatarImage() async {
    var itemId = avatarList.singleWhere((element) => element.active).id;
    Map body = {
      'token': _shared.readString('token'),
      'section': 'avatar',
      'item_id': itemId
    };

    var response = await _api.changeAvatar(body);

    return response.fold<bool>((left) {
      BotToast.showText(text: 'خطا در ارتباط');
      return false;
    }, (right) {
      if (right.data['status']) {
        return true;
      } else {
        BotToast.showText(text: right.data['msg']);
        return false;
      }
    });
  }

  void updateProfile() async {
    if (textEditingController.text.isNotEmpty &&
        textEditingController.text.length > 3) {
      var username = await changeUsername();

      if (username) {
        var avatar = await changeAvatarImage();
        if (avatar) {
          Get.back(result: {'update': true});
        }
      }
    } else {
      var avatar = await changeAvatarImage();
      if (avatar) {
        Get.back(result: {'update': true});
      }
    }
  }
}
