import 'dart:async';
import 'package:audioplayers/audioplayers.dart';
import 'package:get/get.dart';
import 'package:mafia/database/shared/shared_pref_manager.dart';
import 'package:mafia/models/game/find_match/find_match_user_model.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/listeners/find_match/socket_find_match_listeners.dart';
import 'package:mafia/utils/socket/socket_manager.dart';
import 'package:vibration/vibration.dart';

class FindMatchScreenController extends GetxController
    implements SocketFindMatchListeners {
  // injections
  final SocketManager _socket = Get.find();
  final SharedPrefManager _shared = Get.find();
  final HashRouting _router = Get.find();
  final AudioPlayer _audioPlayer = Get.find();

  // variables
  bool appBackground = false;
  var gameFound = false.obs;
  var counter = 6.obs;
  Timer? _timer;
  List<FindMatchUserModel> findMatchUsers =
      List<FindMatchUserModel>.empty(growable: true);

  void navigateToPrevious() async {
    // Get.back();
    Get.until(
      (route) => Get.currentRoute == _router.getRoute('main_btm_nav_holder'),
    );
  }

  void _startFindMatch() {
    var auth = _shared.readBool('auth');
    Map json = {'auth': auth, 'game_type': 'ranked'};
    _socket.getFindMatchSocket.emit('find_match', json);
  }

  Future<void> cancelFindMatch() async {
    _socket.getFindMatchSocket.emit('leave_find', null);
    return await Future.value(null);
  }

  void _startVibrate() async {
    if (await Vibration.hasVibrator() == true) {
      Vibration.vibrate(duration: 1000);
    }
  }

  void fromBackground() async {
    appBackground = true;
  }

  @override
  void onAbandon() async {
    await _socket.getFindMatchSocket.clearListeners();
    Get.until(
        (route) => Get.currentRoute == _router.getRoute('main_btm_nav_holder'));
  }

  @override
  void onFindMatch(data) {
    var socketList = List.from(data['data']);
    if (socketList.isNotEmpty) {
      var users = data['data']
          .map<FindMatchUserModel>((json) => FindMatchUserModel.fromJson(json))
          .toList() as List<FindMatchUserModel>;

      findMatchUsers.clear();
      findMatchUsers.addAll(users);

      update(['findMatchUsers', 'findMatchCount']);
    } else {
      findMatchUsers.clear();
      update(['findMatchUsers', 'findMatchCount']);
    }
  }

  @override
  void onGameFound(data) async {
    gameFound(true);
    update(['gameFound']);
    _startVibrate();
    _audioPlayer.play(AssetSource('match_found.mp3'));
    _runTimer(data['data']['is_creator'], data['data']['game_id']);
  }

  void _runTimer(bool isCreator, String gameId) async {
    if (_timer != null) {
      _timer?.cancel();
    }

    _timer = Timer.periodic(const Duration(seconds: 1), (timer) async {
      if (counter.value == 0) {
        _timer?.cancel();
        // navigation
        try {
          if (Get.currentRoute == _router.getRoute('find_match')) {
            await Get.offAndToNamed(
              _router.getRoute('select_character'),
              arguments: {'is_creator': isClosed, 'gameId': gameId},
            );
          } else {
            // if going to game from home screen while player returned from background
            Get.toNamed(_router.getRoute('select_character'),
                arguments: {'is_creator': isCreator, 'gameId': gameId});
          }
        } catch (_) {
          _socket.disconnect();
          await Future.delayed(const Duration(seconds: 1), () {
            _socket.connect();
          });
        }
      } else {
        counter.value--;
        update(['counter']);
      }
    });
  }

  @override
  void onInit() {
    super.onInit();
    _socket.getFindMatchSocket.addListener(this);
    _startFindMatch();
  }
}
