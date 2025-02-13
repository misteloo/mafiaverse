import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/find_match/socket_find_match_listeners.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketFindMatchService extends GetxService {
  final Socket _socket = Get.find();
  final List<String> _items = [
    'find_match',
    'game_found',
    'abandon',
  ];
  Future<SocketFindMatchService> init() async {
    return this;
  }

  void emit(String key, Map? json) {
    _socket.emit(key, json);
  }

  void addListener(SocketFindMatchListeners listener) async {
    await clearListeners();
    _socket.on('find_match', (data) => listener.onFindMatch(data));
    _socket.on('game_found', (data) => listener.onGameFound(data));
    _socket.on('abandon', (data) => listener.onAbandon());
  }

  Future<void> clearListeners() async {
    return await Future.forEach(_items, (element) => _socket.off(element));
  }
}
