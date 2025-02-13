import 'package:get/get.dart';
import 'package:mafia/utils/socket/listeners/lobby/socket_lobby_create_listeners.dart';
import 'package:socket_io_client/socket_io_client.dart';

class SocketCreateLobbyService extends GetxService {
  Future<SocketCreateLobbyService> init() async => this;
  final List<String> _items = ['lobby_create_result'];
  // injection
  final Socket _socket = Get.find();

  void emit(String key, dynamic value) {
    _socket.emit(key, value);
  }

  void addListener(SocketCreateLobbyListeners listener) async {
    await offSockets();
    _socket.on(
        'lobby_create_result', (data) => listener.createLobbyResult(data));
  }

  Future<void> offSockets() async {
    return await Future.forEach(_items, (element) => _socket.off(element));
  }
}
