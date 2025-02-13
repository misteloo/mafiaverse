import 'package:bot_toast/bot_toast.dart';
import 'package:get/get.dart';
import 'package:mafia/api/api_repository.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/main_controller.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/dialogs/reconnect_to_game_dialog.dart';
import 'package:mafia/utils/socket/listeners/global/socket_global_listeners.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:mafia/utils/widget/custom_snack.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:permission_handler/permission_handler.dart';

class HomeScreenController extends GetxController
    implements SocketGlobalListener {
  // injections
  final SharedPrefManager _shared = Get.find();
  final SocketManager _socket = Get.find();
  final ApiRepository _api = Get.find();
  final HashRouting _router = Get.find();
  final MainController _mainController = Get.find();

  // variables
  String? _userToken;
  bool findMatchLoading = false;
  bool serverUpdate = false;
  bool appRequireUpdate = false;
  bool backSound = true;
  bool freeGame = false;
  String? _appVersion;
  String userGoldCount = '0';
  String userGemCount = '0';

  String get _getUserToken {
    _userToken ??= _shared.readString('token');
    return _userToken!;
  }

  void joinToServer() {
    Map json = {'token': _getUserToken};
    _socket.joinToServer(json);
  }

  void navigateToLocalGame() {
    Get.toNamed(_router.getRoute('local_game'));
  }

  void navigateToMessage() {
    Get.toNamed(_router.getRoute('unread_message'));
  }

  void navigateToLearn() {
    Get.toNamed(_router.getRoute('learn'));
  }

  Future<PackageInfo> _packageInfo() async {
    return await PackageInfo.fromPlatform();
  }

  Future<bool> checkAppVersion() async {
    if (_appVersion == null) {
      showCustomSnack(
          context: Get.context!,
          title: 'خطا',
          body: 'عدم دریافت اطلاعات برنامه');
      return false;
    }
    return true;
  }

  void userHasEnoughGoldToFindMatch({required Function(bool) callback}) async {
    findMatchLoading = true;
    update(['findMatchLoading']);
    Map body = {'token': _getUserToken};
    var response = await _api.findMatchGold(body);
    response.fold((left) {
      BotToast.showText(text: 'خطا در اتصال');
      findMatchLoading = false;
      update(['findMatchLoading']);
      callback(false);
    }, (right) {
      findMatchLoading = false;
      update(['findMatchLoading']);

      if (right.data['status']) {
        callback(right.data['data']['has_enough_gold']);
      } else {
        BotToast.showText(text: 'خطا در اتصال');
        callback(false);
      }
    });
  }

  void backSoundToggle() async {
    backSound = await _mainController.backSoundToggle();
    update(['backSound']);
  }

  void findMatch() async {
    // find match
    await Get.toNamed(_router.getRoute('find_match'));
  }

  @override
  onJoinStatus(data) async {
    await _shared.writeData<String>('user_id', data['data']['user_id']);
    await _shared.writeData<bool>('auth', data['data']['auth']);
    freeGame = data['data']['free_game'] ?? false;
    findMatchLoading = false;
    update(['findMatchLoading', 'freeGame']);
  }

  @override
  onAppDetail(data) async {
    serverUpdate = data['data']['server_update'];
    _appVersion = data['data']['v'];
    if (serverUpdate) {
      update(['serverUpdate']);
    }
    // app update
    var packageInfo = await _packageInfo();

    appRequireUpdate = _appVersion != packageInfo.version;

    if (appRequireUpdate) {
      update(['appUpdate']);
    }
  }

  @override
  onUserGold(data) {
    userGoldCount = data['data']['gold'].toString();
    update(['userAsset']);
  }

  @override
  onReconnectToGame(data) {
    print(data);
    var parse = data['data'];
    bool isPlayer = parse['is_player'];
    bool isSupervisor = parse['is_supervisor'];
    String gameId = parse['game_id'];
    String? character = parse['character'];

    // show dialog
    if (isPlayer || isSupervisor) {
      Get.dialog(
          ReconnectToGameDialog(
            character: character,
            exitCallback: () {
              if (Get.isDialogOpen ?? false) {
                Get.back();
              }

              Map<String, dynamic> json = {'game_id': gameId};
              _socket.emit('abandon', json);
            },
            reconnectCallback: () {
              if (Get.isDialogOpen ?? false) {
                Get.back();
              }

              Future.delayed(const Duration(milliseconds: 500), () {
                // navigate to reconnect page
                Get.toNamed(_router.getRoute('reconnect'),
                    arguments: {'gameId': gameId});
              });
            },
          ),
          barrierDismissible: false);
    }
  }

  void requestPermissions() async {
    await [
      Permission.location,
      Permission.storage,
    ].request();

    await Permission.microphone.isDenied.then((value) async {
      if (value) {
        await Permission.microphone.request();
      }
    });
  }

  void microphonePermissionGranted(Function(bool) callback) {
    Permission.microphone.isGranted.then((value) => callback(value));
  }

  void checkSocketConnection() {
    if (Get.currentRoute == 'main_btm_nav_holder' && !_socket.status()) {
      _socket.connect();
    }
  }

  @override
  void onInit() {
    _socket.globalListener(this);
    // permission
    requestPermissions();
    // join
    joinToServer();
    super.onInit();
  }
}
