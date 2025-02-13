import 'package:get/get.dart';
import 'package:mafia/utils/constants/hash_routing.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_create_service.dart';
import 'package:mafia/utils/socket/services/find_match/socket_find_match_service.dart';
import 'package:mafia/utils/socket/listeners/global/socket_global_listeners.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_game_service.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_service.dart';
import 'package:mafia/utils/socket/services/local/socket_local_game_service.dart';
import 'package:mafia/utils/socket/services/nato_game/socket_nato_game_service.dart';
import 'package:mafia/utils/socket/services/reconnect/socket_reconnect_service.dart';
import 'package:mafia/utils/socket/services/nato_game/socket_select_character_service.dart';
import 'package:mafia/utils/socket/services/lobby/socket_lobby_waiting_service.dart';
import 'package:socket_io_client/socket_io_client.dart';

import '../dialogs/dc_dialog.dart';

class SocketManager extends GetxService {
  final Socket _socket = Get.find();
  final SocketFindMatchService _findMatchSocket = Get.find();
  final SocketSelectCharacterService _selectCharacterSocket = Get.find();
  final SocketNatoGameService _natoGameSocket = Get.find();
  final SocketReconnectService _reconnectSocket = Get.find();
  final SocketLocalGameService _localGameSocket = Get.find();
  final SocketCreateLobbyService _createLobbySocket = Get.find();
  final SocketWaitingLobbyService _waitingLobbySocket = Get.find();
  final SocketLobbyService _lobbySocket = Get.find();
  final SocketLobbyGameService _lobbyGame = Get.find();

  final HashRouting _router = Get.find();

  Map? _userToken;
  final List<String> _items = [
    'join_status',
    'user_gold',
    'app_detail',
    'reconnect_notification',
  ];

  Future<SocketManager> init() async {
    watchSocketConnection();

    return this;
  }

  void connect() {
    _socket.connect();
  }

  void disconnect() {
    _socket.disconnect();
  }

  bool status() {
    return _socket.connected;
  }

  void watchSocketConnection() {
    _socket.onDisconnect((data) async {
      String currentRoute = _router.reverseRoute(Get.currentRoute);
      if (currentRoute == 'login' ||
          currentRoute == 'signup' ||
          currentRoute == 'confirm_code' ||
          currentRoute == 'splash') {
        return;
      }
      if (Get.isDialogOpen == true) {
        Get.back();
      }

      if (Get.isBottomSheetOpen == true) {
        Get.back();
      }

      // back to home page when dc
      await Future.delayed(const Duration(milliseconds: 500), () {
        Get.until((route) =>
            Get.currentRoute == _router.getRoute('main_btm_nav_holder'));
      });

      // clear socket list
      await getFindMatchSocket.clearListeners();
      await getNatoGameSocket.clearSockets();
      await getWaitingLobbySocket.clearSockets();
      await getLobbyGameSocket.clearListeners();
      // show dc dialog
      await Get.dialog(const DcDialog(), barrierDismissible: false);
    });

    _socket.onReconnect((data) {
      String currentRoute = _router.reverseRoute(Get.currentRoute);
      if (currentRoute == 'login' ||
          currentRoute == 'signup' ||
          currentRoute == 'confirm_code') {
        return;
      }

      if (Get.isDialogOpen == true) {
        Get.back(closeOverlays: true);
      }
      if (_getUserToken != null) {
        joinToServer(_getUserToken);
      }
    });
  }

  set _setUserToken(Map token) {
    _userToken = token;
  }

  get _getUserToken => _userToken;

  SocketFindMatchService get getFindMatchSocket => _findMatchSocket;
  SocketSelectCharacterService get getSelectCharacterSocket =>
      _selectCharacterSocket;
  SocketNatoGameService get getNatoGameSocket => _natoGameSocket;

  SocketReconnectService get getReconnectSocket => _reconnectSocket;

  SocketLocalGameService get getLocalGameSocket => _localGameSocket;

  SocketCreateLobbyService get getCreateLobbySocket => _createLobbySocket;

  SocketWaitingLobbyService get getWaitingLobbySocket => _waitingLobbySocket;

  SocketLobbyService get getLobbySocket => _lobbySocket;

  SocketLobbyGameService get getLobbyGameSocket => _lobbyGame;

  void joinToServer(Map token) {
    _setUserToken = token;
    _socket.emit('join', token);
  }

  void _userGold(Map token) {
    _socket.emit('user_gold', token);
  }

  void _appDetail() {
    _socket.emit('app_detail');
  }

  void globalListener(SocketGlobalListener global) {
    _clearSocket();
    _socket.on('join_status', (data) {
      global.onJoinStatus(data);
      _appDetail();
      _userGold(_getUserToken);
    });
    _socket.on('user_gold', (data) => global.onUserGold(data));
    _socket.on('app_detail', (data) => global.onAppDetail(data));
    _socket.on(
        'reconnect_notification', (data) => global.onReconnectToGame(data));
  }

  void _clearSocket() {
    for (var item in _items) {
      _socket.off(item);
    }
  }

  void emit(String key, dynamic value) {
    _socket.emit(key, value);
  }
}
